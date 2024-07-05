<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 찾기</title>
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/members/reset-password.css">

    <script>
        $(document).ready(function () {
            $('.modal').on('hidden.bs.modal', function (e) {
                console.log('modal close');
                $(this).find('form')[0].reset();
            });
            $("#checkEmail").click(function () {
                let userEmail = $("#userEmail").val();
                $.ajax({
                    type: "GET",
                    url: "/api/v1/public/member/forgot-password",
                    data: { "userEmail": userEmail },
                    success: function (response) {
                        if (response.result) {
                            Swal.fire({
                                title: '발송 완료!',
                                text: '입력하신 이메일로 임시 비밀번호가 발송되었습니다.',
                                icon: 'success'
                            }).then((OK) => {
                                if (OK) {
                                    // 비동기 메일 발송 요청
                                    $.ajax({
                                        type: "POST",
                                        url: "/api/v1/public/member/send-email/update-password",
                                        data: { "userEmail": userEmail },
                                        success: function () {
                                            console.log('임시 비밀번호 발송 성공');
                                        },
                                        error: function (xhr) {
                                            console.error('임시 비밀번호 발송 실패', xhr);
                                        }
                                    });
                                    window.location.href = "/api/v1/view/member/"; // 로그인 화면으로 이동
                                }
                            });
                        } else {
                            $('#checkMsg').html('<p style="color:red">일치하는 정보가 없습니다.</p>');
                        }
                    },
                    error: function(xhr, status, error) {
                        $('#checkMsg').html('<p style="color:red">오류가 발생했습니다. 다시 시도해주세요.</p>');
                    }
                });
            });
        });
    </script>

    <script>
        function cancelButtonClicked() {
            window.location.href = "/api/v1/view/member/";
        }
    </script>
</head>
<body>
<div class="container">
    <div class="modal fade" id="myModal" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4><span class="glyphicon glyphicon-lock"></span> 비밀번호 찾기</h4>
                </div>
                <div class="modal-body">
                    <div style="color: #ac2925; text-align: center;">입력된 정보로 임시 비밀번호가 전송됩니다.</div>
                    <hr>
                    <form role="form">
                        <div class="form-group">
                            <label for="userEmail">Email</label>
                            <input type="text" class="form-control" id="userEmail" placeholder="가입시 등록한 이메일을 입력하세요.">
                        </div>
                    </form>
                    <hr>
                    <div class="text-center small mt-2" id="checkMsg" style="color: red"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" onclick="cancelButtonClicked()">나가기</button>
                    <button type="button" class="btn btn-primary" id="checkEmail">보내기</button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
