<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>글작성</title>
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>
    <script>
        function handleSubmit() {
            console.log('handling submit request')
            const formData = {
                writer: $('input[name="writer"]').val(),
                title: $('input[name="title"]').val(),
                content: $('textarea[name="content"]').val()
            };

            $.ajax({
                url: "/api/v1/article/save",
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
                },
                success: function(response) {
                    alert('글이 성공적으로 작성되었습니다.');
                    window.location.href = "/api/v1/view/article/paging";
                },
                error: function(xhr, status, error) {
                    alert('글 작성에 실패했습니다.');
                }
            });
        }
    </script>
</head>
<body>
    <form id="articleForm">
        writer: <input type="text" name="writer" value="${nickname}" readonly> <br>
        title: <input type="text" name="title"> <br>
        content: <textarea name="content" cols="30" rows="10"></textarea> <br>
        <button onclick="handleSubmit()">등록하기333</button>
    </form>
</body>
</html>