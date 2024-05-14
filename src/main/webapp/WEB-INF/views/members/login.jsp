<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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

    <title>프로젝트!</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }

        .note {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 500px;
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            grid-gap: 15px;
            align-items: center;
            justify-items: center;
            padding-top: 50px;
            padding-bottom: 50px;
        }

        .menu-title {
            grid-column: span 2;
            font-size: 24px;
            color: black;
            margin-bottom: 20px;
            font-weight: bold;
        }

        button {
            background-color: #007bff;
            color: white;
            padding: 15px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
        }

        button:hover {
            background-color: #0056b3;
        }
    </style>

    <script>
        function logout() {
            // JWT 토큰 삭제
            localStorage.removeItem('jwt');
            // 로그아웃 후 홈페이지로 리디렉션
            window.location.href = "/";
        }

        function editProfile() {
            $.ajax({
                url: "/api/v1/view/member/edit-form",
                type: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (htmlContent) {
                    // 서버로부터 받은 HTML 콘텐츠를 현재 페이지의 DOM에 삽입
                    document.body.innerHTML = htmlContent; // 현재 페이지 바디에 폼 삽입
                    $(document).ready(function () {
                        init();

                        function init() {
                            $("#btn-update").on("click", function () {
                                checkCurrentPassword();
                            });
                        }

                        function checkCurrentPassword() {
                            let currentPassword = $("#currentPassword").val();

                            $(".error-message").remove();

                            $.ajax({
                                url: "/api/v1/public/member/check-current-password",
                                type: "POST",
                                data: { password: currentPassword },
                                headers: {
                                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                                },
                                success: function (resp) {
                                    console.log("Response:", resp);

                                    if (resp.result) {
                                        update();
                                    } else {
                                        let errorMessage = "현재 비밀번호가 일치하지 않거나, 입력하지 않는 문구가 있습니다.";
                                        $("#currentPassword").after("<div class='error-message'>" + errorMessage + "</div>");
                                    }
                                },
                                error: function (xhr, status, error) {
                                    console.error("Error checking current password:", xhr.responseText);
                                    let errorMessage = "An error occurred. Please try again.";
                                    $("#currentPassword").after("<div class='error-message'>" + errorMessage + "</div>");
                                }
                            });
                        }

                        function update() {
                            let data = {
                                id: $("#id").val(),
                                name: $("#name").val(),
                                password: $("#password").val(),
                                nickname: $("#nickname").val()
                            };

                            console.log("업데이트 데이터:", data);

                            $.ajax({
                                type: "PUT",
                                url: "/api/v1/member",
                                data: JSON.stringify(data),
                                contentType: "application/json",
                                headers: {
                                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                                }
                            })
                                .done(function (resp) {
                                    alert("회원 수정이 완료되었습니다.");
                                    window.location.href = "/";
                                })
                                .fail(function (error) {
                                    console.log("업데이트 실패:", error.responseJSON);
                                    let errorMessage = error.responseJSON.message || "업데이트 중 오류가 발생했습니다.";

                                    // 현재 비밀번호 입력 필드 바로 다음에 오류 메시지가 표시되도록 처리
                                    if ($("#currentPassword + .error-message").length === 0) {
                                        $("#currentPassword").after("<div class='error-message'>" + errorMessage + "</div>");
                                    }
                                    alert(errorMessage);
                                });
                        }
                    });
                },
                error: function (error) {
                    console.error(error);
                    // 에러 처리, 예를 들어 사용자에게 알림을 보여주는 등
                    alert('프로필 정보를 가져오는데 실패했습니다.');
                }
            });
        }

        function deletePage() {
            window.location.href = "/api/v1/view/member/delete-page";
        }

        function studyRecruitmentArticlePaging() {
            window.location.href = "/api/v1/view/article/paging";
        }

        function StudyGroupView() {
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

        function profileView() {
            $.ajax({
                type: "GET",
                url: "/api/v1/profile/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    var memberId = response.memberId;
                    if (memberId === null || memberId === '') {
                        alert('회원 ID가 존재하지 않습니다.');
                        return;
                    }
                    window.location.href = "/api/v1/view/profile/view?memberId=" + memberId;
                },
                error: function (xhr) {
                    alert("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }
    </script>
</head>
<body>
<div class="note">
    <div class="menu-title">메뉴</div>
    <button onclick="logout()">로그아웃</button>
    <button onclick="editProfile()">회원수정</button>
    <button onclick="deletePage()">회원탈퇴</button>
    <button onclick="profileView()">프로필 & 친구 목록</button>
    <button onclick="studyRecruitmentArticlePaging()">스터디 모집 게시판</button>
    <button onclick="StudyGroupView()">스터디 그룹</button>
</div>
</body>
</html>
