<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <title>스터디 그룹 생성</title>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        header {
            text-align: center;
            margin-bottom: 20px;
        }

        h2 {
            color: #4A90E2;
            font-size: 28px;
            font-weight: bold;
            margin: 20px 0;
        }

        .container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 500px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }

        textarea {
            height: 260px;
            resize: vertical;
        }

        input[type="text"], textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }

        input[type="submit"] {
            width: 100%;
            background-color: #4A90E2;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #357ABD;
        }

        .button-container {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .button-container input[type="submit"],
        .button-container button {
            flex: 0 0 auto;
            width: auto;
            padding: 9px 20px;
            font-size: 17px;
        }

        button {
            background-color: #4A90E2;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        button:hover {
            background-color: #357ABD;
        }
    </style>

    <script>
        $(document).ready(function() {
            $("#createStudyGroupForm").submit(function(e) {
                e.preventDefault();
                var groupName = $("#groupName").val();
                var studyTopic = $("#studyTopic").val();

                $.ajax({
                    url: "/api/v1/study-group",
                    type: "POST",
                    contentType: "application/json",
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                    },
                    data: JSON.stringify({
                        groupName: groupName,
                        studyTopic: studyTopic
                    }),
                    success: function(response) {
                        alert('스터디 그룹이 성공적으로 생성되었습니다.');
                        StudyGroupMember();
                    },
                    error: function(xhr, status, error) {
                        console.error("스터디 그룹 생성에 실패했습니다.", 'error');
                    }
                });
            });
        });

        function StudyGroupMember() {
            $.ajax({
                type: "GET",
                url: "/api/v1/study-group/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    var memberId = response.memberId;
                    if (memberId === null || memberId === '') {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    window.location.href = "/api/v1/view/study-group/?memberId=" + memberId;
                },
                error: function (xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }

        function home() {
            window.history.back();
        }
    </script>
</head>
<body>
<div class="container">
    <header>
        <h2>스터디 그룹 생성</h2>
    </header>
    <form id="createStudyGroupForm">
        <label for="groupName">그룹 이름:</label>
        <input type="text" id="groupName" name="groupName" required>

        <label for="studyTopic">스터디 주제:</label>
        <textarea id="studyTopic" name="studyTopic" required></textarea>

        <div class="button-container">
            <button type="button" onclick="home()">이전으로</button>
            <input type="submit" value="생성하기">
        </div>
    </form>
</div>
</body>
</html>
