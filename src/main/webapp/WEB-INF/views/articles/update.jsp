<%@ page import="dev.linkcentral.presentation.request.article.ArticleUpdateRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>update</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/articles/update.css">

    <script>
        $(document).ready(function () {
            var articleId = $("#id").val();

            $.ajax({
                url: "/api/v1/public/article/" + articleId,
                type: "GET",
                success: function (response) {
                    // 성공적으로 데이터를 받아온 후 input 필드에 값을 채웁니다.
                    $("#writerId").val(response.articleDetails.member.id);
                    $("#writer").val(response.articleDetails.writer);
                    $("#title").val(response.articleDetails.title);
                    $("#content").val(response.articleDetails.content);
                },
                error: function (error) {
                    console.error("게시글 로드 중 오류가 발생했습니다.", error);
                }
            });
        });

        function updateArticle() {
            const id = $("#id").val();
            const writerId = $("#writerId").val();
            const writer = $("#writer").val();
            const title = $("#title").val();
            const content = $("#content").val();

            const data = {
                id: id,
                writerId: writerId,
                writer: writer,
                title: title,
                content: content
            };

            $.ajax({
                url: "/api/v1/article",
                type: "PUT",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                data: JSON.stringify(data),
                contentType: "application/json",
                success: function (response) {
                    console.log("글이 업데이트되었습니다.");
                    window.location.href = `/api/v1/view/article/paging?page=${page}`;
                },
                error: function (error) {
                    console.error("글 업데이트 중 오류가 발생했습니다.", error);
                    alert("글 업데이트 중 오류가 발생했습니다. " + error.responseText);
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
    <h3 class="text-center mb-4" style="font-weight: 600;">게시글 수정</h3>
    <form name="updateForm">
        <input type="hidden" id="id" value="${articleUpdate.id}">
        <input type="hidden" id="writerId">
        <div class="form-group">
            <label for="writer">작성자:</label>
            <input type="text" id="writer" class="form-control" value="${articleUpdate.writer}" readonly>
        </div>
        <div class="form-group">
            <label for="title">스터디 제목:</label>
            <input type="text" id="title" class="form-control" value="${articleUpdate.title}">
        </div>
        <div class="form-group">
            <label for="content">스터디 상세 내용:</label>
            <textarea id="content" class="form-control" rows="5">${articleUpdate.content}</textarea>
        </div>
        <div class="form-group button-container">
            <button type="button" class="menu-button" onclick="home()">이전으로</button>
            <input type="button" class="update-button" value="수정하기" onclick="updateArticle()">
        </div>
    </form>
</div>
</body>
</html>
