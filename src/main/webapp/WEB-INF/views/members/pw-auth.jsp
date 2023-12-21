<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>이메일 비밀번호 인증</title>
</head>
<body>

<form action="pw_set.me" method="post">
    <input type="hidden" name="num" value="${num }">
    <div class="content">
        <div class="textbox">
            <input type="text" name="email_injeung" placeholder="인증번호를 입력하세요">
            <label>인증번호</label>
            <div class="error"></div>
        </div>
        <br><br>
        <input type="submit" id="check" value="확인">
    </div>
</form>

</body>
</html>
