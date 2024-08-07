<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.database.entity.studygroup.StudyGroup" %>
<%@ page import="dev.linkcentral.database.entity.studygroup.StudyGroup" %>
<% Boolean showCreateButton = (Boolean) request.getAttribute("showCreateButton"); %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/studys/study-group.css">
    <title>스터디 그룹 페이지</title>

    <script type="text/javascript">
        var userId = ${member.id};
    </script>

    <script>
        var GroupId;

        $(document).ready(function() {
            $.ajax({
                url: "/api/v1/study-group/my-groups/ids",
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                type: "GET",
                success: function(response) {
                    var listHtml = '<ul class="dynamic-text">';
                    $.each(response.studyGroupIds, function(index, studyGroupId) {
                        GroupId = studyGroupId;
                        // listHtml += '<li class="group-name">스터디 그룹 ID: <span>' + studyGroupId + '</span></li>';
                        $('#joinRequestsTable .btn-primary, #joinRequestsTable .btn-danger').show();
                    });
                    listHtml += '</ul>';
                    $('#studyGroupList').html(listHtml);

                    if (GroupId) {
                        loadJoinRequests(GroupId);
                    }
                },
                error: function(xhr, status, error) {
                    $('#studyGroupList').html('<p class="dynamic-text">스터디 그룹 정보를 가져오는데 실패했습니다.</p>');
                    console.error("Error: " + status + " " + error);
                }
            });
        });
    </script>

    <script>
        // 스터디 그룹 가입 요청 목록을 불러오는 함수
        function loadJoinRequests(groupId) {
            console.log("받아 왔나? --> GroupId: " + groupId);
            $.ajax({
                url: '/api/v1/study-group/' + groupId + '/received-requests',
                type: 'GET',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(response) {
                    var requests = response.studyMemberRequests || [];
                    var requestsHtml = requests.map(function(request) {
                        return '<tr class="dynamic-text">' +
                            '<td>' + request.memberName + '</td>' +
                            '<td>' + request.groupName + '</td>' +
                            '<td>' +
                            '<button class="btn-primary" onclick="acceptJoinRequest(' + groupId + ', ' + request.id + ')">수락</button>' +
                            '<button class="btn-danger" onclick="rejectJoinRequest(' + groupId + ', ' + request.id + ')">거절</button>' +
                            '</td>' +
                            '</tr>';
                    }).join('');
                    $('#joinRequestsTable tbody').html(requestsHtml);
                },
                error: function(xhr, status, error) {
                    console.error("가입 요청 목록을 불러오는데 실패했습니다.", status, error);
                }
            });
        }

        // 가입 요청 수락 함수
        function acceptJoinRequest(groupId, requestId) {
            $.ajax({
                url: '/api/v1/study-group/' + groupId + '/membership-requests/' + requestId + '/accept',
                method: 'POST',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function() {
                    alert('가입 요청이 수락되었습니다.');
                    loadJoinRequests(groupId);
                    window.location.reload();
                },
                error: function(xhr) {
                    alert('가입 요청 수락에 실패했습니다. 오류: ' + xhr.responseText);
                }
            });
        }

        // 가입 요청 거절 함수
        function rejectJoinRequest(groupId, requestId) {
            $.ajax({
                url: '/api/v1/study-group/' + groupId + '/membership-requests/' + requestId + '/reject',
                method: 'POST',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function() {
                    alert('가입 요청이 거절되었습니다.');
                    loadJoinRequests(groupId);
                },
                error: function(xhr) {
                    alert('가입 요청 거절에 실패했습니다. 오류: ' + xhr.responseText);
                }
            });
        }
    </script>


    <%--가입된 스터디원--%>
    <script>
        $(document).ready(function() {
            loadAcceptedStudyGroups();
            checkStudyGroupExistenceAndUpdateUI();

            $("body").on("click", ".leaveGroup", function() {
                var studyGroupId = $(this).data("studygroupid");
                if (studyGroupId) {
                    leaveGroup(studyGroupId);
                } else {
                    console.error("StudyGroupId가 설정되지 않았습니다.");
                }
            });
        });

        // 스터디 그룹 탈퇴 함수 분리
        function leaveGroup(studyGroupId) {
            $.ajax({
                url: "/api/v1/study-group/" + studyGroupId + "/leave",
                type: "DELETE",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(response) {
                    if (response.success) {
                        alert(response.message);
                        location.reload(); // 페이지를 새로고침하여 모든 변경 사항을 반영
                    } else {
                        alert("스터디 그룹을 탈퇴하지 못했습니다.: " + response.message);
                    }
                },
                error: function(xhr) {
                    alert("스터디 그룹을 떠나는 중 오류 발생: " + xhr.responseText);
                }
            });
        }
    </script>

    <script>
        $(document).ready(function() {
            loadAcceptedStudyGroups();
        });

        function loadAcceptedStudyGroups() {
            $.ajax({
                url: "/api/v1/study-group/current-accepted",
                type: "GET",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(response) {
                    $("#studyGroupsAndMembers").empty();
                    $("#currentGroupsTable tbody").empty();
                    if (response.acceptedStudyGroupDetails && Array.isArray(response.acceptedStudyGroupDetails)) {
                        response.acceptedStudyGroupDetails.forEach(function(group) {
                            var groupHtml = '<div class="study-group">';
                            groupHtml += '<h3 class="group-name">' + group.groupName + '</h3>';
                            groupHtml += '<ul>';
                            if (group.members && Array.isArray(group.members)) {
                                group.members.forEach(function(member) {
                                    groupHtml += '<li>' + member.name;
                                    if (member.id === group.leaderId) {
                                        groupHtml += ' (리더)';
                                    }
                                    groupHtml += '</li>';
                                });
                            }
                            groupHtml += '</ul></div>';
                            $("#studyGroupsAndMembers").append(groupHtml);

                            var rowHtml = '<tr class="dynamic-text">' +
                                '<td class="group-name">' + group.groupName + '</td>' +
                                '<td>' + group.memberCount + '명 (리더 포함)</td>' +
                                '<td class="text-right">' +
                                '<div class="btn-group">' +
                                '<button class="btn-danger leaveGroup" data-studygroupid="' + group.id + '">탈퇴</button>' +
                                '</div>' +
                                '</td>' +
                                '</tr>';
                            $('#currentGroupsTable tbody').append(rowHtml);
                        });
                    } else {
                        console.error("acceptedStudyGroupDetails가 배열이 아닙니다.");
                    }
                    $("#createStudyGroupButton").show();
                },
                error: function(xhr, status, error) {
                    console.error("그룹 목록을 불러오는 데 실패했습니다: ", xhr.responseText);
                    $("#createStudyGroupButton").show(); // 실패 시에도 버튼 표시
                }
            });
        }

        function checkStudyGroupExistenceAndUpdateUI() {
            $.ajax({
                url: "/api/v1/study-group/exists?userId=" + userId,
                type: "GET",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(response) {
                    if (response.exists) {
                        $("#createStudyGroupButton").hide();
                        $("#alreadyCreatedMessage").show();
                    } else {
                        $("#createStudyGroupButton").show();
                        $("#alreadyCreatedMessage").hide();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("스터디 그룹 존재 여부 확인 중 에러 발생: ", xhr.responseText);
                }
            });
        }
    </script>

    <script>
        $(document).ready(function() {
            console.log("userId: " + userId);
            $.ajax({
                url: "/api/v1/study-group/user/" + userId + "/groups-with-members",
                type: 'GET',
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                success: function(response) {
                    var groupsAndMembers = response.groupMembersDetailDTOS;
                    var contentHtml = '';

                    groupsAndMembers.forEach(function(group) {
                        contentHtml += '<div class="study-group">';
                        contentHtml += '<h3>' + group.groupName + '</h3>';

                        if (group.leaderId === userId) {
                            // 로그인한 유저가 리더인 경우
                            contentHtml += '<ul>';
                            group.members.forEach(function(member) {
                                contentHtml += '<li>';

                                if (member.id === group.leaderId) {
                                    // 멤버가 리더인 경우, 이름 옆에 '리더' 표시
                                    contentHtml += member.name + ' (리더)';
                                } else if (member.id !== userId) {
                                    // 멤버가 로그인한 유저(자신)가 아닌 경우
                                    contentHtml += member.name + ' <button class="btn-danger" onclick="expelMember(' + group.id + ', ' + member.id + ')" style="display:none;">내보내기</button>';
                                } else {
                                    // 로그인한 유저 자신인 경우, 이름만 출력
                                    contentHtml += member.name;
                                }
                                contentHtml += '</li>';
                            });
                            contentHtml += '</ul>';
                        } else {

                            // 로그인한 유저가 리더가 아닌 경우, 멤버 이름만 출력
                            contentHtml += '<ul>';
                            group.members.forEach(function(member) {
                                contentHtml += '<li>';
                                if (member.id === group.leaderId) {
                                    contentHtml += member.name + ' (리더)';
                                } else {
                                    contentHtml += member.name;
                                }
                                contentHtml += '</li>';
                            });
                            contentHtml += '</ul>';
                        }
                        contentHtml += '</div>';
                    });
                    $('#studyGroupsAndMembers').html(contentHtml);
                },
                error: function(xhr, status, error) {
                    console.error("스터디 그룹 및 구성원을 로드하지 못했습니다.", status, error, xhr.responseText);
                }
            });
        });


        function expelMember(groupId, memberId) {
            if (confirm('이 멤버를 추방하시겠습니까?')) {
                $.ajax({
                    url: '/api/v1/study-group/' + groupId + '/members/' + memberId + '/expel',
                    type: 'DELETE',
                    headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                    success: function() {
                        alert('멤버가 추방되었습니다.');
                        $('#member-' + memberId).remove();
                        window.location.reload();
                    },
                    error: function(xhr, status, error) {
                        alert('멤버 추방에 실패했습니다.');
                        console.error("Error: " + status + " " + error);
                    }
                });
            }
        }

        function home() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <header>
        <h2>스터디 그룹</h2>
    </header>
    <div id="createStudyGroupButton" class="button-container">
        <button class="btn-primary" onclick="home()">나가기</button>
        <button class="btn-primary" onclick="window.location.href='/api/v1/view/study-group/create'">그룹 생성</button>
    </div>

    <div id="studyGroupList">
        <!-- 스터디 그룹 목록 표시 -->
    </div>

    <h5>가입 요청 확인</h5>
    <table id="joinRequestsTable">
        <thead>
        <tr>
            <th>가입 요청 유저</th>
            <th>가입 요청 그룹</th>
            <th class="text-right">요청 여부</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="request" items="${joinRequests}">
            <tr class="dynamic-text">
                <td>${request.memberName}</td>
                <td class="group-name">${request.groupName}</td>
                <td class="text-right">
                    <div class="btn-group">
                        <button class="btn-primary" onclick="acceptJoinRequest(${request.groupId}, ${request.id})" style="display:none;">수락</button>
                        <button class="btn-danger" onclick="rejectJoinRequest(${request.groupId}, ${request.id})" style="display:none;">거절</button>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <br/><br/>

    <h5>가입 그룹 확인</h5>
    <div id="currentGroupsSection">
        <table id="currentGroupsTable">
            <thead>
            <tr>
                <th>그룹 이름</th>
                <th>현재 인원</th>
                <th class="text-right">요청 여부</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="group" items="${acceptedStudyGroups}">
                <tr class="dynamic-text">
                    <td class="group-name">${group.groupName}</td>
                    <td>${group.memberCount}명(자신 포함)</td>
                    <td class="text-right">
                        <div class="btn-group">
                            <button class="btn-danger leaveGroup" data-studygroupid="${group.id}" style="display:none;">탈퇴</button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <c:if test="${not empty error}">
        <div id="errorMsg" class="alert dynamic-text">
                ${error}
        </div>
    </c:if>

    <br/>

    <h4>그룹 현황</h4>
    <div id="studyGroupsAndMembers" class="group-container dynamic-text">
        <c:forEach var="group" items="${acceptedStudyGroups}">
            <div class="study-group">
                <h3 class="group-name">${group.groupName}</h3>
                <ul>
                    <c:forEach var="member" items="${group.members}">
                        <li>${member.name} <c:if test="${member.id == group.leaderId}">(리더)</c:if></li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>
    <p id="noGroupsMessage" class="text-light-red" style="display:none;">가입된 스터디 그룹이 없습니다.</p>
</div>
</body>
</html>
