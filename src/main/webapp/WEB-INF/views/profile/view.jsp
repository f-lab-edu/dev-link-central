<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>프로필 보기</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- jQuery library -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

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
        var memberId;

        function initEventListeners(userId) {
            $('#sendFriendRequestBtn').click(function () {
                var receiverId = $(this).data('receiver-id');
                sendFriendRequest(receiverId);
            });

            $(document).on('click', '.accept-friend-request', function () {
                var requestId = $(this).data('request-id');
                acceptFriendRequest(requestId, userId);
            });

            $(document).on('click', '.reject-friend-request', function () {
                var requestId = $(this).data('request-id');
                rejectFriendRequest(requestId, userId);
            });

            $('#unfriendBtn').click(function () {
                var friendId = $(this).data('friend-id');
                unfriend(friendId, userId);
            });
        }

        function sendFriendRequest(senderId, receiverId) {
            $.ajax({
                type: 'POST',
                url: "/api/v1/friends/request",
                contentType: "application/json",
                data: JSON.stringify({ senderId: senderId, receiverId: receiverId }),
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(response) {
                    alert('친구 요청이 성공적으로 전송되었습니다.');
                    fetchAndDisplayUserInfo(senderId);
                },
                error: function(xhr, status, error) {
                    alert('친구 요청을 보내지 못했습니다. 오류: ' + error);
                }
            });
        }

        function acceptFriendRequest(requestId, userId) {
            $.ajax({
                url: '/api/v1/friends/accept/' + requestId,
                type: 'POST',
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function() {
                    alert('친구 요청을 수락했습니다.');
                    fetchAndDisplayUserInfo(userId); // Fetch and display user info again to reflect the new friend status
                },
                error: function(error) {
                    console.error("친구 요청 수락에 실패했습니다.", error);
                }
            });
        }

        function rejectFriendRequest(requestId, userId) {
            // AJAX를 사용하여 친구 요청을 거절하는 요청을 서버에 전송
            $.ajax({
                url: '/api/v1/friends/reject/' + requestId,
                type: 'DELETE',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function () {
                    alert('친구 요청을 거절했습니다.');
                    fetchAndDisplayUserInfo(userId); // 사용자 정보를 새로고침하여 친구 요청 상태 업데이트
                },
                error: function (error) {
                    console.error("친구 요청 거절에 실패했습니다.", error);
                }
            });
        }

        function unfriend(friendId, userId) {
            // AJAX 요청을 사용하여 친구 관계를 해제
            $.ajax({
                url: '/api/v1/friends/' + friendId,
                type: 'DELETE',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function () {
                    alert('친구 관계가 해제되었습니다.');
                    fetchAndDisplayUserInfo(userId); // 사용자 정보를 새로고침하여 친구 관계 상태 업데이트
                },
                error: function (error) {
                    console.error("친구 관계 해제에 실패했습니다.", error);
                }
            });
        }
    </script>


    <%-- 프로필 수정 --%>
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
                    memberId = response.memberId; // 전역 변수에 memberId를 설정합니다.
                    if (!memberId) {
                        console.log("회원 정보를 가져올 수 없습니다.");
                        return;
                    }

                    // 로그인한 사용자 ID를 페이지에 표시
                    $("#loggedInUserId").text("로그인한 사용자 ID: " + memberId);
                    console.log("id 가져 왔다: " + memberId);

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

    <%-- 올바른 hidden input for user ID --%>
    <input type="hidden" id="loggedInUserIdValue" value="<c:out value='${loggedInUserId}'/>"/>

    <!-- 로그인한 사용자의 ID를 저장하기 위한 숨겨진 입력 필드 -->
    <input type="hidden" id="userId" value="${loggedInUserId}"/>

    <!-- 조건부 이미지 표시 -->
    <img class="profile-image" src="${not empty profile.imageUrl ? profile.imageUrl : '/images/default.png'}" alt="Profile Image">
    <p class="profile-bio">소개: ${profile.bio}</p>

    <!-- 로그인한 사용자 ID 출력 -->
    <p id="loggedInUserId">로그인한 사용자 ID: <span><c:out value="${loggedInUserId}"/></span></p>

    <!-- 프로필 소유자의 ID 출력 -->
    <p>프로필 사용자 ID: ${profile.memberId}</p>

    <div id="editButtonContainer"></div>

    <!-- 친구 관계에 따라 버튼 표시 -->
    <%-- 서버 측에서 친구 관계 상태 설정 필요 --%>
    <button id="sendFriendRequestBtn" data-receiver-id="<c:out value="${viewedMemberId}"/>" class="profile-button">친구 요청</button>
    <button id="unfriendBtn" data-friend-id="<c:out value="${friendId}"/>" class="profile-button">친구 끊기</button>

    <div class="friend-requests-container">
        <h2>받은 친구 요청</h2>
        <div id="friendRequests"></div> <!-- 받은 친구 요청을 동적으로 표시할 곳 -->
    </div>
</div>
</body>
</html>