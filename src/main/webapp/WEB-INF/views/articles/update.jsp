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

    <style>
        body {
            background-color: #f8f9fa;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            font-weight: bold;
        }

        textarea {
            resize: none;
        }

        .form-control {
            border-radius: 5px;
        }

        input[type=button] {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            border: none;
            color: white;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type=button]:hover {
            background-color: #0056b3;
        }
    </style>

    <script>
        $(document).ready(function () {
            var articleId = $("#id").val();

            $.ajax({
                url: "/api/v1/public/article/" + articleId,
                type: "GET",
                success: function (response) {
                    // 성공적으로 데이터를 받아온 후 input 필드에 값을 채웁니다.
                    $("#writer").val(response.articleDetails.writer);
                    $("#title").val(response.articleDetails.title);
                    $("#content").val(response.articleDetails.content);
                },
                error: function (error) {
                    console.error("게시글 로드 중 오류가 발생했습니다.", error);
                }
            });
        });
    </script>

    <script>
        function updateArticle() {
            const id = $("#id").val();
            const writer = $("#writer").val();
            const title = $("#title").val();
            const content = $("#content").val();

            const data = {
                id: id,
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
                    // window.location.href = "/api/v1/view/article/" + id;
                },
                error: function (error) {
                    console.error("글 업데이트 중 오류가 발생했습니다.", error);
                }
            });
        }
    </script>
</head>
<body>
<div class="container">
    <h3 class="text-center mb-4" style="font-weight: 600;">게시글 수정</h3>
    <form name="updateForm">
        <input type="hidden" id="id" value="${articleUpdate.id}">
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
        <div class="form-group">
            <input type="button" value="게시글 업데이트" onclick="updateArticle()">
        </div>
    </form>
</div>
</body>
</html>
