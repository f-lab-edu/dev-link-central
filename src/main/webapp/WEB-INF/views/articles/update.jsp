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
        url: "/article/update",
        type: "PUT",
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function(response) {
          console.log("글이 업데이트되었습니다.");
          location.href = "/article/";
        },
        error: function(error) {
          console.error("글 업데이트 중 오류가 발생했습니다.");
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