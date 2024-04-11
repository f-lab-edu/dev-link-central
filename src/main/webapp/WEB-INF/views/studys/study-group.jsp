<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.linkcentral.database.entity.StudyGroup" %>
<%@ page import="java.util.List" %>
<% Long studyGroupId = (Long) request.getAttribute("studyGroupId"); %>
<% StudyGroup studyGroup = (StudyGroup) request.getAttribute("studyGroup"); %>
<%@ page import="dev.linkcentral.database.entity.StudyMember" %>
<%@ page import="dev.linkcentral.database.entity.StudyGroup" %>
<% Boolean showCreateButton = (Boolean) request.getAttribute("showCreateButton"); %>
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

    <meta charset="UTF-8">
    <title>스터디 그룹 페이지</title>
    <!-- 추가적인 스타일시트 및 스크립트 -->
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            margin: 0;
            padding: 0;
            line-height: 1.6;
        }
        header {
            background-color: #007bff;
            color: #fff;
            padding: 10px 0;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        table, th, td {
            border: 1px solid #dee2e6;
        }
        th, td {
            text-align: left;
            padding: 8px;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            margin-right: 5px;
        }
        button:hover {
            background-color: #218838;
        }
        button:active {
            background-color: #1e7e34;
        }
        ul {
            list-style-type: none;
            padding: 0;
        }
        li {
            padding: 8px;
            background-color: #f2f2f2;
            border: 1px solid #ddd;
            margin-top: -1px; /* Remove space between the items */
        }

        #alreadyCreatedMessage {
            color: #007bff; /* 글자색 변경 */
            font-size: 19px; /* 글자 크기 변경 */
        }

        #errorMsg {
            color: #dc3545; /* 글자색 변경 */
            font-size: 17px; /* 글자 크기 변경 */
        }
    </style>

    <script type="text/javascript">
        var userId = ${member.id};
    </script>

    <script>
        var GroupId;

        $(document).ready(function() {
            $.ajax({
                url: "/api/v1/study-group/study-group-id",
                headers: {'Authorization': 'Bearer ' + localStorage.getItem("jwt")},
                type: "GET",
                success: function(data) {
                    var listHtml = '<ul>';
                    $.each(data, function(index, studyGroupId) {
                        GroupId = studyGroupId;
                        listHtml += '<li>스터디 그룹 ID: ' + studyGroupId + '</li>';
                    });
                    listHtml += '</ul>';
                    $('#studyGroupList').html(listHtml);

                    if (GroupId) {
                        loadJoinRequests(GroupId);
                    }
                },
                error: function(xhr, status, error) {
                    $('#studyGroupList').html('<p>스터디 그룹 정보를 가져오는데 실패했습니다.</p>');
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
                success: function(requests) {
                    var requestsHtml = requests.map(function(request) {
                        return '<tr>' +
                            '<td>' + request.memberName + '</td>' +
                            '<td>' + request.groupName + '</td>' +
                            '<td>' +
                            '<button onclick="acceptJoinRequest(' + groupId + ', ' + request.id + ')">수락</button>' +
                            '<button onclick="rejectJoinRequest(' + groupId + ', ' + request.id + ')">거절</button>' +
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
            $("body").on("click", ".leaveGroup", function() {
                console.log("탈퇴 버튼 클릭 이벤트 실행됨");
                var studyGroupId = $(this).data("studyGroupId");
                $.ajax({
                    url: "/api/v1/study-group/" + studyGroupId + "/leave",
                    type: "DELETE",
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                    success: function() {
                        alert("스터디 그룹에서 탈퇴했습니다.");
                        loadAcceptedStudyGroups();
                        window.location.reload();
                    },
                    error: function(xhr, status, error) {
                        alert("탈퇴 처리에 실패했습니다: " + xhr.responseText);
                    }
                });
            });
        });
    </script>

    <script>
        $(document).ready(function() {
            loadAcceptedStudyGroups();
            checkStudyGroupExistenceAndUpdateUI();
        });

        function loadAcceptedStudyGroups() {
            $.ajax({
                url: "/api/v1/study-group/current-accepted",
                type: "GET",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(groups) {
                    $("#currentGroupsTable tbody").empty();
                    if (groups.length === 0) {
                        $("#noGroupsMessage").show(); // 스터디 그룹이 없을 경우 메시지를 보여준다.
                    } else {
                        $("#noGroupsMessage").hide(); // 스터디 그룹이 있을 경우 메시지를 숨긴다.
                        groups.forEach(function(group) {
                            var row = $("<tr>").append(
                                $("<td>").text(group.groupName),
                                $("<td>").append(
                                    $("<button>").addClass("leaveGroup").text("탈퇴").data("studyGroupId", group.id)
                                )
                            );
                            $("#currentGroupsTable tbody").append(row);
                        });
                    }
                },
                error: function(xhr, status, error) {
                    console.error("그룹 목록을 불러오는 데 실패했습니다: ", xhr.responseText);
                }
            });
        }

        function checkStudyGroupExistenceAndUpdateUI() {
            $.ajax({
                url: "/api/v1/study-group/exists?userId=" + userId,
                type: "GET",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(exists) {
                    if (exists) {
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
        function checkStudyGroupExists(userId) {
            $.ajax({
                url: "/api/v1/study-group/exists?userId=" + userId,
                type: "GET",
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
                success: function(isStudyGroupCreated) {
                    if(isStudyGroupCreated) {
                        $("#createStudyGroupButton").hide();
                        $("#alreadyCreatedMessage").show();
                    } else {
                        $("#createStudyGroupButton").show();
                        $("#alreadyCreatedMessage").hide();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("스터디 그룹 존재 여부 확인 중 에러 발생: ", status, error);
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
                success: function(groupsAndMembers) {
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
                                    contentHtml += member.name + ' <button onclick="expelMember(' + group.id + ', ' + member.id + ')">내보내기</button>';
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
                    console.error("Failed to load study groups and members", status, error);
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
    </script>

</head>
<body>
<header>
    <h1>스터디 그룹</h1>
</header>

<% if (Boolean.TRUE.equals(showCreateButton)) { %>
<div id="createStudyGroupButton">
    <button onclick="window.location.href='/api/v1/view/study-group/create'">스터디 그룹 생성</button>
</div>
<% } else { %>
<div id="alreadyCreatedMessage">
    <p>이미 스터디 그룹을 생성했습니다.</p>
</div>
<% } %>


<div id="createStudyGroupButton" style="display:none;">
    <button onclick="window.location.href='/api/v1/view/study-group/create'">스터디 그룹 생성</button>
</div>
<div id="alreadyCreatedMessage" style="display:none;">
    <p>이미 스터디 그룹을 생성했습니다.</p>
</div>

<br/>
<h2>나의 스터디 그룹으로 가입 요청을 보낸 사람들의 목록</h2>
<!-- 가입 요청 목록을 표시할 테이블 -->
<table id="joinRequestsTable">
    <thead>
    <tr>
        <th>신청한 회원 이름</th>
        <th>신청한 그룹 이름</th>
        <th>요청 여부</th>
    </tr>
    </thead>
    <tbody>
    <!-- 가입 요청 목록이 삽입 -->
    </tbody>
</table>

<br/>

<h2>현재 가입 되어 있는 스터디 그룹</h2>
<div id="currentGroupsSection">
    <table id="currentGroupsTable">
        <thead>
        <tr>
            <th>그룹 이름</th>
            <th>요청 여부</th>
        </tr>
        </thead>
        <tbody>
        <!-- 서버로부터 받은 스터디 그룹 목록 동적으로 삽입 -->
        </tbody>
    </table>
</div>

<c:if test="${not empty error}">
    <div id="errorMsg" class="alert alert-danger">
            ${error}
    </div>
</c:if>

<br/>


<!-- 스터디 그룹과 멤버를 동적으로 렌더링할 요소 -->
<h2>개별 그룹 현황</h2>
<div id="studyGroupsAndMembers"></div>


</body>
</html>
