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
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/studys/create.css">
    <title>스터디 그룹 생성</title>

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
