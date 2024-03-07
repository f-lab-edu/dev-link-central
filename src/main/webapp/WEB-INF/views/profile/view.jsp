<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>프로필 보기</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">

    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous"></script>

    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .profile-container {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            text-align: center;
            width: 50%;
            max-width: 600px;
        }

        .profile-image {
            width: 200px;
            height: 200px;
            border-radius: 50%;
            margin-bottom: 30px;
            border: 3px solid #ddd;
        }

        .profile-bio {
            margin-top: 20px;
            font-size: 18px;
            color: #333;
            line-height: 1.6;
        }

        .profile-button {
            display: block;
            margin-top: 10px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
        }

        .profile-button:hover {
            background-color: #0056b3;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        .edit-button {
            margin-top: 20px;
            background-color: #4CAF50;
        }

        .edit-button:hover {
            background-color: #45a049;
        }
    </style>

    <script type="text/javascript">
        $(document).ready(function () {
            var loggedInUserId = <c:out value="${loggedInUserId}" />;
            var viewedMemberId = <c:out value="${viewedMemberId}" />;
            var friendId;

            // 친구 관계 ID 조회
            function fetchFriendId() {
                $.ajax({
                    type: 'GET',
                    url: "/api/v1/friends/friendship-ids",
                    data: {senderId: loggedInUserId, receiverId: viewedMemberId},
                    success: function (response) {
                        friendId = response;
                    },
                    error: function () {
                        console.log('친구 관계 ID 조회 실패');
                    }
                });
            }

            // 페이지 로드 시 친구 관계 ID 조회
            fetchFriendId();

            // '친구 끊기' 버튼 이벤트
            $('#unfriendBtn').click(function () {
                if (friendId) {
                    $.ajax({
                        type: 'DELETE',
                        url: "/api/v1/friends/" + friendId,
                        success: function () {
                            alert('친구 관계가 해제되었습니다.');
                            // 여기에 페이지 리로드나 다른 UI 업데이트 로직 추가 가능
                        },
                        error: function () {
                            alert('친구 관계 해제 실패.');
                        }
                    });
                } else {
                    alert('친구 관계 ID가 없습니다.');
                }
            });
        });

        function goBack() {
            window.history.back();
        }
    </script>


    <script type="text/javascript">
        $(document).ready(function () {
            fetchAndDisplayUserInfo();
        });

        function fetchAndDisplayUserInfo() {
            $.ajax({
                type: "GET",
                url: "/api/v1/profile/auth/member-info",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    var memberId = response.memberId; // 로그인한 사용자의 memberId

                    // memberId가 존재하지 않는 경우 "null" 문자열을 할당하지 않고, 로직을 계속 진행
                    if (memberId === null || memberId === '') {
                        console.log("회원 정보를 가져올 수 없습니다.");
                        return;
                    }

                    // 로그인한 사용자 ID를 페이지에 표시
                    $("#loggedInUserId").text("로그인한 사용자 ID: " + memberId);

                    // 프로필 소유자의 ID
                    var profileMemberId = "${profile.memberId}";

                    // 프로필 수정 버튼 조건부 표시
                    if (String(memberId) === String(profileMemberId)) {
                        // 조건이 충족될 경우, 프로필 수정 버튼을 동적으로 생성하여 페이지에 삽입
                        $("#editButtonContainer").html('<a href="/api/v1/view/profile/edit?memberId=' + profileMemberId + '" class="profile-button edit-button">프로필 수정</a>');
                    }
                },
                error: function (xhr) {
                    console.log("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }
    </script>
</head>
<body>
<div class="profile-container">
    <h1>프로필</h1>
    <c:if test="${not empty loggedInUserName}">
        <h2>사용자 이름: ${loggedInUserName}</h2>
    </c:if>

    <!-- 조건부 이미지 표시 -->
    <img class="profile-image" src="${not empty profile.imageUrl ? profile.imageUrl : '/images/default.png'}" alt="Profile Image">
    <p class="profile-bio">소개: ${profile.bio}</p>

    <!-- 로그인한 사용자 ID 출력 -->
    <p id="loggedInUserId">로그인한 사용자 ID:</p>

    <!-- 프로필 소유자의 ID 출력 -->
    <p>프로필 사용자 ID: ${profile.memberId}</p>

    <div id="editButtonContainer"></div>

    <!-- 친구 관계에 따라 버튼 표시 -->
    <%-- 서버 측에서 친구 관계 상태 설정 필요 --%>
    <button id="sendFriendRequestBtn" class="profile-button">친구 요청</button>
    <button id="unfriendBtn" class="profile-button">친구 끊기</button>
</div>

<!-- 이전 화면으로 돌아가기 버튼 -->
<button onclick="goBack()" class="profile-button">이전 화면으로</button>
</body>
</html>