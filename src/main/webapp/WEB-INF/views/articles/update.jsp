<%@ page import="dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>update</title>

  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

  <script>
    $(document).ready(function() {
      var articleId = $("#id").val(); // 기존에 설정한 hidden input에서 id 값을 가져옵니다.

      $.ajax({
        url: "/api/v1/public/article/" + articleId,
        type: "GET",
        success: function(article) {
          // 성공적으로 데이터를 받아온 후 input 필드에 값을 채웁니다.
          $("#writer").val(article.writer);
          $("#title").val(article.title);
          $("#content").val(article.content);
        },
        error: function(error) {
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
        success: function(response) {
          console.log("글이 업데이트되었습니다.");
          window.location.href = `/api/v1/view/article/paging?page=${page}`;
        },
        error: function(error) {
          console.error("글 업데이트 중 오류가 발생했습니다.", error);
        }
      });
    }
  </script>

</head>
<body>
<form name="updateForm">
  <input type="hidden" id="id" value="${articleUpdate.id}">

  writer: <input type="text" id="writer" value="${articleUpdate.writer}" readonly><br>
  title: <input type="text" id="title" value="${articleUpdate.title}"><br>
  contents: <textarea id="content" cols="30" rows="10">${articleUpdate.content}</textarea><br>

  <input type="button" value="글수정" onclick="updateArticle()">
</form>
</body>
</html>