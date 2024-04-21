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

        .friend-requests-container, .friends-list-container {
            margin-top: 20px;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .friend-requests-container h2, .friends-list-container h2 {
            color: #007bff;
        }

        .friend-request, .friend-item {
            padding: 10px;
            margin-bottom: 5px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .friend-request span, .friend-item span {
            font-size: 16px;
            color: #333;
        }

        .friend-checkbox {
            margin-right: 10px;
        }

        #unfriendSelectedBtn {
            margin-top: 20px;
            background-color: #dc3545;
            border-color: #dc3545;
        }

        #unfriendSelectedBtn:hover {
            background-color: #c82333;
            border-color: #bd2130;
        }

        #sendFriendRequestBtn, #unfriendBtn {
            margin-bottom: 10px;
        }

        /* 수락 버튼 스타일 */
        .accept-friend-request {
            padding: 5px 10px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            margin-right: 5px;
        }

        .accept-friend-request:hover {
            background-color: #218838;
        }

        /* 거절 버튼 스타일 */
        .reject-friend-request {
            padding: 5px 10px;
            background-color: #dc3545;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .reject-friend-request:hover {
            background-color: #c82333;
        }

        .friend {
            padding: 10px;
            margin-bottom: 10px;
            background-color: #ffffff;
            border: 1px solid #dddddd;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .friend span {
            font-size: 16px;
            color: #333;
        }

        .friend-checkbox {
            margin-right: 15px;
        }
    </style>

    <script type="text/javascript">
        var memberId;

        function initEventListeners(userId) {
            $('#sendFriendRequestBtn').off('click').on('click', function() {
                var receiverId = $(this).data('receiver-id');
                sendFriendRequest(memberId, receiverId);
            });

            $(document).on('click', '.accept-friend-request', function () {
                var requestId = $(this).data('request-id');
                console.log("Accepting friend request with ID: " + requestId); // 요청 ID 확인
                if (requestId) {
                    acceptFriendRequest(requestId);
                } else {
                    console.error("Request ID is undefined."); // 요청 ID가 undefined라면 오류 메시지 출력
                }
            });

            $(document).off('click', '.reject-friend-request').on('click', '.reject-friend-request', function () {
                var requestId = $(this).data('request-id');
                var userId = $('#userId').val(); // 로그인한 사용자의 ID를 가져온다.
                rejectFriendRequest(requestId, userId);
            });

            // "친구 끊기" 버튼 클릭 이벤트
            $(document).on('click', '.unfriendBtn', function() {
                var friendId = $(this).data('friend-id');
                console.log("Clicked unfriendBtn with friendId:", friendId);
                if (friendId) {
                    unfriend(friendId);
                } else {
                    console.error("Friend ID is undefined.");
                }
            });
        }

        // 페이지 준비 완료 후 한 번만 이벤트 리스너를 초기화
        $(document).ready(function() {
            initEventListeners();
        });

        function sendFriendRequest(senderId, receiverId) {
            $.ajax({
                type: 'POST',
                url: "/api/v1/friends/request",
                contentType: "application/json",
                data: JSON.stringify({senderId: senderId, receiverId: receiverId}),
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function (response) {
                    alert('친구 요청이 성공적으로 전송되었습니다.');

                    var friendRequestId = response.friendRequestId;
                    console.log("@@ --> friendRequestId: " + friendRequestId);

                    updateUnfriendButton(friendRequestId);
                },
                error: function (xhr, status, error) {
                    var errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;
                    alert('친구 요청을 보내지 못했습니다. 오류: ' + errorMessage);
                }
            });
        }

        function updateUnfriendButton(friendId) {
            $.ajax({
                url: '/api/v1/friends/friendship-ids',
                type: 'GET',
                data: {senderId: memberId, receiverId: userId},
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(response) {
                    // 친구 관계의 ID로 버튼을 업데이트
                    $('#unfriendBtn').data('friend-id', response).attr('data-friend-id', response).show();
                },
                error: function(xhr, status, error) {
                    console.error("친구 관계 ID 조회에 실패했습니다.", xhr.responseText);
                }
            });
        }

        function acceptFriendRequest(requestId, buttonElement) {
            if (!requestId) {
                console.error("Request ID is undefined.");
                return;
            }

            // 버튼을 비활성화하여 추가 클릭 방지
            $(buttonElement).prop('disabled', true).text('처리중...');

            $.ajax({
                url: '/api/v1/friends/accept/' + requestId,
                type: 'POST',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function () {
                    alert('친구 요청을 수락했습니다.');

                    // 친구 목록을 새로고침하는 함수를 호출
                    fetchAndDisplayFriends();

                    // 요청이 수락되면, 서버로부터 친구 관계 ID를 받아와야 한다.
                    // 이 ID를 받기 위해 별도의 함수를 호출
                    updateFriendButtonAfterAcceptance(memberId, $(buttonElement).data('sender-id'));
                    $(buttonElement).closest('.friend-request').remove(); // 요청을 DOM에서 제거
                },
                error: function (xhr, status, error) {
                    console.error("친구 요청 수락에 실패했습니다. 오류: " + xhr.responseText);
                    $(buttonElement).prop('disabled', false).text('수락');
                }
            });
        }

        function updateFriendButtonAfterAcceptance(memberId, otherUserId) {
            if (!memberId || !otherUserId) {
                return;
            }

            $.ajax({
                url: '/api/v1/friends/friendship-ids',
                type: 'GET',
                data: {
                    senderId: memberId, // 로그인한 사용자의 ID
                    receiverId: otherUserId // 다른 사용자(요청을 보낸 사용자)의 ID
                },
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(friendId) {
                    // 친구 관계 ID가 있으면 'unfriend' 버튼의 'data-friend-id' 속성을 업데이트하고 버튼을 보여준다.
                    if (friendId) {
                        $('#unfriendBtn').data('friend-id', friendId).attr('data-friend-id', friendId).show();
                    } else {
                        // 친구 관계 ID가 없으면, 버튼을 숨긴다.
                        $('#unfriendBtn').hide();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("친구 관계 ID 조회에 실패했습니다. 오류: " + xhr.responseText);
                    // 친구 관계 ID를 찾을 수 없는 경우 버튼을 숨긴다.
                    $('#unfriendBtn').hide();
                }
            });
        }

        var isRejectingRequest = false; // 요청 거절 처리 중인지 확인하는 플래그

        function rejectFriendRequest(requestId, userId) {
            if (isRejectingRequest) {
                return; // 함수를 종료하여 추가 요청을 방지
            }

            isRejectingRequest = true; // 요청 거절 처리 시작

            $.ajax({
                url: '/api/v1/friends/reject/' + requestId,
                type: 'DELETE',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function () {
                    alert('친구 요청을 거절했습니다.');
                    fetchAndDisplayUserInfo(userId);
                    isRejectingRequest = false; // 요청 거절 처리 완료 후 플래그를 다시 false로 설정
                },
                error: function (error) {
                    console.error("친구 요청 거절에 실패했습니다.", error);
                    isRejectingRequest = false; // 오류 발생 시 플래그를 다시 false로 설정
                }
            });
        }

        function unfriend(friendId) {
            if (!friendId) {
                console.error("Cannot unfriend without friendId.");
                return;
            }
            console.log("Attempting to unfriend with friendId:", friendId);

            $.ajax({
                url: '/api/v1/friends/' + friendId,
                type: 'DELETE',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function() {
                    alert('친구 관계가 해제되었습니다.');
                    location.reload(); // 성공 후 페이지 새로고침
                },
                error: function(xhr, status, error) {
                    console.error("친구 관계 해제에 실패했습니다.", xhr.responseText);
                }
            });
        }

        // 페이지 로딩 완료 시 이벤트 리스너 초기화
        $(document).ready(function() {
            initEventListeners();
        });

        // 친구 요청을 불러오는 함수 추가
        function fetchAndDisplayReceivedFriendRequests() {
            if (!memberId) {
                console.error("memberId가 정의되지 않았습니다.");
                return;
            }

            $.ajax({
                type: "GET",
                url: "/api/v1/friends/received-requests/" + memberId,
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function (response) {
                    console.log("Response:", response);

                    if (response.length > 0) {
                        var requestsHtml = response.map(function(request) {
                            console.log('Entire request object:', request);

                            // request.id를 사용하여 data-request-id를 설정
                            return '<div class="friend-request">' +
                                '<span>From: ' + request.senderName + '</span>' +
                                '<button data-request-id="' + request.id + '" class="accept-friend-request">수락</button>' +
                                '<button data-request-id="' + request.id + '" class="reject-friend-request">거절</button>' +
                                '</div>';
                        }).join('');

                        // HTML을 DOM에 삽입
                        $('#friendRequests').html(requestsHtml);
                    } else {
                        $('#friendRequests').html('<p>받은 친구 요청이 없습니다.</p>');
                    }
                    bindRequestButtons();
                },
                error: function (xhr) {
                    console.error("친구 요청 수락에 실패했습니다. 오류: " + xhr.responseText);
                }
            });
        }

        // 이벤트 위임을 사용하도록 수정
        function bindRequestButtons() {
            $(document).off('click', '.accept-friend-request').on('click', '.accept-friend-request', function() {
                var requestId = $(this).attr('data-request-id');
                if (requestId) {
                    acceptFriendRequest(requestId, this); // 현재 클릭된 버튼 요소를 함수에 전달
                } else {
                    console.error("Request ID is undefined.");
                }
            });

            $(document).on('click', '.reject-friend-request', function() {
                var requestId = $(this).data('request-id');
                rejectFriendRequest(requestId, memberId);
            });
        }

        function updateFriendButton(profileMemberId) {
            if (!memberId || !profileMemberId) {
                return;
            }

            // AJAX를 통해 친구 관계 ID를 가져오는 함수
            $.ajax({
                url: '/api/v1/friends/friendship-ids',
                type: 'GET',
                data: {
                    senderId: memberId,
                    receiverId: profileMemberId
                },
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(friendId) {
                    if (friendId) {
                        $('#unfriendBtn').data('friend-id', friendId).attr('data-friend-id', friendId);
                    } else {
                        $('#unfriendBtn').hide();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("친구 관계 ID 가져오기에 실패했습니다. 상태: " + status + ", 오류: " + error);
                }
            });
        }
    </script>

    <script type="text/javascript">
        $(document).ready(function() {
            // 친구 끊기 폼 제출 처리
            $('#unfriendForm').submit(function(event) {
                event.preventDefault();

                // 체크된 모든 체크박스 수집
                var selectedFriends = $('.friend-checkbox:checked').map(function() {
                    return $(this).val();
                }).get();

                // 선택된 친구들에 대해 친구 끊기 요청 보내기
                unfriendSelected(selectedFriends);
            });
        });

        // 친구 끊기 요청을 보내는 함수
        function unfriendSelected(friendshipIds) {
            if (!friendshipIds.length) {
                alert('먼저 친구를 선택해주세요.');
                return;
            }

            console.log("친구 선택 했는가: " + friendshipIds);

            $.ajax({
                url: '/api/v1/friends/unfriend',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(friendshipIds),
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function() {
                    friendshipIds.forEach(function(id) {
                        $('input[data-friendship-id="' + id + '"]').closest('.friend').remove();
                    });
                    alert('선택한 친구 관계가 해제되었습니다.');
                },
                error: function(xhr, status, error) {
                    console.error("친구 끊기에 실패했습니다.", xhr.responseText);
                }
            });
        }

        $(document).on('click', '#unfriendSelectedBtn', function() {
            // 체크된 모든 체크박스에서 친구 관계 ID를 가져온다.
            var selectedFriendshipIds = $('input[name="selectedFriends"]:checked').map(function() {
                // data-friendship-id 속성을 제대로 읽어오는지 확인
                var friendshipId = $(this).data('friendship-id');
                console.log("현재 체크박스의 friendshipId: ", friendshipId);
                return friendshipId;
            }).get();

            // 로그 출력을 위한 코드를 추가
            $('input[name="selectedFriends"]:checked').each(function() {
                var friendshipId = $(this).data('friendship-id');
                console.log("Checked item friendshipId: ", friendshipId); // 각 체크된 항목의 friendshipId가 출력한다.
            });

            // 수집된 친구 관계 ID로 친구 끊기 요청을 보낸다.
            unfriendSelected(selectedFriendshipIds);
        });

        // 사용자 정보를 가져오는 함수
        $(document).ready(function() {
            var memberId;

            // 사용자 정보를 가져오는 함수
            function fetchUserInfo() {
                $.ajax({
                    url: '/api/v1/member/info',
                    type: 'GET',
                    headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                    success: function(response) {
                        console.log("사용자 정보 받기 성공:", response);
                        memberId = response.userId;

                        // 사용자 ID를 사용하여 필요한 함수들을 호출
                        initEventListeners();
                        fetchAndDisplayFriends();
                    },
                    error: function(xhr, status, error) {
                        console.error("사용자 정보 요청에 실패했습니다.", xhr.responseText);
                    }
                });
            }

            fetchUserInfo();
        });

        function fetchAndDisplayFriends() {
            if (!memberId) {
                console.error("memberId가 정의되지 않았습니다.");
                return;
            }

            $.ajax({
                url: '/api/v1/friends/' + memberId + '/friendships',
                type: 'GET',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(friendships) {
                    // status가 'ACCEPTED'인 관계만을 필터링
                    var acceptedFriendships = friendships.filter(function(friendship) {
                        return friendship.status === 'ACCEPTED';
                    });

                    var friendsHtml = acceptedFriendships.map(function(friendship) {
                        var friendName = friendship.senderId === memberId ? friendship.receiverName : friendship.senderName;
                        console.log(friendship); // 콘솔 로그로 확인

                        return '<div class="friend friend-item">' +
                            '<input type="checkbox" class="friend-checkbox" name="selectedFriends" value="' + friendName +
                            '" data-friendship-id="' + friendship.friendshipId + '"> ' +
                            '<span>' + friendName + '</span>' +
                            '</div>';
                    }).join('');

                    $("#friendsList").html(friendsHtml);
                },
                error: function(xhr, status, error) {
                    console.error("친구 목록을 가져오는 데 실패했습니다. 오류: " + xhr.responseText);
                }
            });
        }

        // 페이지 로딩 완료 시 사용자 정보 요청과 함께 친구 목록을 가져오는 함수를 호출한다.
        $(document).ready(function() {
            fetchUserInfo();
        });


        $(document).ready(function() {
            memberId = $("#userId").val(); // 로그인한 사용자의 ID를 가져 온다.
            initEventListeners(memberId); // memberId를 인자로 넘겨 초기화
            fetchAndDisplayFriends(memberId); // 여기에서 호출하여 친구 목록을 표시
        });
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
                    memberId = response.memberId;
                    console.log("memberId after setting: " + memberId);

                    if (memberId) {
                        console.log("memberId를 가져왔다: " + memberId);
                        fetchAndDisplayReceivedFriendRequests();

                        // 프로필 소유자의 ID
                        var profileMemberId = "${profile.memberId}";

                        // 프로필 수정 버튼 조건부 표시
                        if (String(memberId) === String(profileMemberId)) {
                            $("#editButtonContainer").html('<a href="/api/v1/view/profile/edit?memberId=' + profileMemberId + '" class="profile-button edit-button">프로필 수정</a>');
                        } else {
                            // 다른 사용자의 프로필을 보는 경우, 친구 관계 확인을 위해 updateFriendButton 호출
                            updateFriendButton(profileMemberId);
                        }
                    } else {
                        console.error("회원 정보를 가져올 수 없습니다.");
                    }

                    // 로그인한 사용자 ID를 페이지에 표시
                    $("#loggedInUserId").text("로그인한 사용자 ID: " + memberId);
                    console.log("id 가져 왔다: " + memberId);
                },
                error: function (xhr) {
                    console.log("회원 정보를 가져올 수 없습니다: " + xhr.responseText);
                }
            });
        }

        var userId; // 전역 변수 선언

        // 페이지 로딩 완료 시 이벤트 리스너 초기화
        $(document).ready(function () {
            userId = $('#userId').val(); // 로그인한 사용자의 ID를 설정
            initEventListeners();
        });
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
    <img class="profile-image" src="${not empty profile.imageUrl ? profile.imageUrl : '/images/default.png'}"
         alt="Profile Image">
    <p class="profile-bio">소개: ${profile.bio}</p>

    <!-- 로그인한 사용자 ID 출력 -->
    <p id="loggedInUserId">로그인한 사용자 ID: <span><c:out value="${loggedInUserId}"/></span></p>

    <!-- 프로필 소유자의 ID 출력 -->
    <p>프로필 사용자 ID: ${profile.memberId}</p>

    <div id="editButtonContainer"></div>

    <!-- 친구 관계에 따라 버튼 표시 -->
    <button id="sendFriendRequestBtn" data-receiver-id="<c:out value="${viewedMemberId}"/>" class="profile-button">친구 요청</button>

    <div class="friend-requests-container">
        <h2>요청 받은 친구 목록</h2>
        <div id="friendRequests">
            <!-- 받은 친구 요청이 여기에 동적으로 삽입 -->
        </div>
    </div>

    <div class="friends-list-container">
        <h2>친구 목록</h2>
        <form id="unfriendForm">
            <div id="friendsList">
                <!-- 친구 목록이 동적으로 삽입됩니다. 예시: -->
                <!-- <div class="friend-item"><input type="checkbox" name="friendId" value="친구ID"> 친구이름</div> -->
            </div>
            <button type="button" id="unfriendSelectedBtn" class="profile-button">선택한 친구 끊기</button>
        </form>
    </div>
</div>
</body>
</html>
