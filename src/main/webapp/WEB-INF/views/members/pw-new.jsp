<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>새 비밀번호 설정</title>
    <script>
        function isSame() {
            var pw1 = document.getElementById("pw").value;
            var pw2 = document.getElementById("pw2").value;
            var span = document.getElementById("same");

            if (pw1 !== pw2) {
                span.innerHTML = "비밀번호가 일치하지 않습니다.";
            } else {
                span.innerHTML = "";
            }
        }
    </script>
</head>
<body>

<form action="pw_new.me" method="post" class="content">
    <div class="textbox">
        <input id="pw" name="pw" type="password">
        <label>새 비밀번호</label>
        <div class="error">
            Invalid password
        </div>
    </div>
    <div class="textbox">
        <input id="pw2" type="password" onchange="isSame();">
        <label>새 비밀번호 확인</label>
        <div class="error">
            Invalid password
        </div>
    </div>
    <span id="same"></span>
    <br><br>
    <input type="submit" id="check" value="비밀번호 변경">
    <input type="hidden" name="email" value="<%=email %>">
</form>

</body>
</html>
