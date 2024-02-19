<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">

    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <title>프로젝트!</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        header {
            background-color: #333;
            color: white;
            padding: 10px;
            text-align: center;
        }

        .container {
            margin: 20px;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            margin: 5px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        p {
            font-size: 18px;
        }
    </style>


    <script>
        function logout() {
            // JWT 토큰 삭제
            localStorage.removeItem('jwt');
            // 로그아웃 후 홈페이지로 리디렉션
            window.location.href = '/';
        }

        function editProfile() {
            $.ajax({
                type: "GET",
                url: '/api/v1/view/member/edit-form',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    window.location.href = response.url;
                },
                error: function (xhr) {
                    alert("회원 정보 수정 페이지로 이동할 수 없습니다: " + xhr.responseText);
                }
            });
        }

        function deletePage() {
            window.location.href = "/api/v1/view/member/delete-page";
        }

        function studyRecruitmentArticlePaging() {
            window.location.href = "/api/v1/view/article/paging";
        }

        function profileView() {
            $.ajax({
                type: "GET",
                url: "/api/v1/profile/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    var memberId = response.memberId;
                    if (memberId === null || memberId === '') {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                },
                error: function(xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }
    </script>
</head>
<body>
<header>
    <h1>프로젝트!</h1>
</header>
<div class="container">
    <button onclick="logout()">로그아웃</button>

    <button onclick="editProfile()">회원수정</button>

    <button onclick="deletePage()">회원탈퇴</button>

    <button onclick="profileView()">프로필 보기</button>

    <button onclick="studyRecruitmentArticlePaging()">스터디 모집 게시판 페이징 목록</button>
</div>
</body>
</html>
