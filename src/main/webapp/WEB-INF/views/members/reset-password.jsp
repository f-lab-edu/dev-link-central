<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 찾기</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">

    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <style>
        body {
            background-color: #f8f9fa;
        }

        .container {
            margin-top: 50px;
        }

        .modal-header {
            background-color: #007bff;
            color: #fff;
            text-align: center;
        }

        .modal-body {
            background-color: #fff;
        }

        #checkMsg {
            margin-top: 10px;
        }
    </style>

    <script>
        $(document).ready(function () {
            $('.modal').on('hidden.bs.modal', function (e) {
                console.log('modal close');
                $(this).find('form')[0].reset();
            });
            $("#checkEmail").click(function () {
                let userEmail = $("#userEmail").val();
                let userName = $("#userName").val();
                $.ajax({
                    type: "GET",
                    url: "/api/v1/member/forgot-password",
                    data: {
                        "userEmail": userEmail,
                        "userName": userName
                    },
                    success: function (res) {
                        if (res['result']) {
                            Swal.fire({
                                title: '발송 완료!',
                                text: '입력하신 이메일로 임시비밀번호가 발송되었습니다.',
                                icon: 'success'
                            }).then((OK) => {
                                if (OK) {
                                    $.ajax({
                                        type: "POST",
                                        url: "/api/v1/member/send-email/update-password",
                                        data: {
                                            "userEmail": userEmail,
                                            "userName": userName
                                        }
                                    });
                                    window.location.href = "/api/v1/view/member/"; // 로그인 화면으로 이동
                                }
                            });
                            $('#checkMsg').html('<p style="color:darkblue"></p>');
                        } else {
                            $('#checkMsg').html('<p style="color:red">일치하는 정보가 없습니다.</p>');
                        }
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
    <!-- Modal -->
    <div class="modal fade" id="myModal" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <h4><span class="glyphicon glyphicon-lock"></span> 비밀번호 찾기</h4>
                </div>
                <div class="modal-body">
                    <div style="color: #ac2925">
                        <center>입력된 정보로 임시 비밀번호가 전송됩니다.</center>
                    </div>
                    <hr>
                    <form role="form">
                        <div class="form-group">
                            <label for="userEmail"><span class="glyphicon glyphicon-user"></span> Email</label>
                            <input type="text" class="form-control" id="userEmail" placeholder="가입시 등록한 이메일을 입력하세요.">
                        </div>
                        <div class="form-group">
                            <label for="userName"><span class="glyphicon glyphicon-eye-open"></span> Name</label>
                            <input type="text" class="form-control" id="userName" placeholder="가입시 등록한 이름을 입력하세요.">
                        </div>
                        <button type="button" class="btn btn-success btn-block" id="checkEmail">OK</button>
                    </form>
                    <hr>
                    <div class="text-center small mt-2" id="checkMsg" style="color: red"></div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-danger btn-default pull-left" data-dismiss="modal" onclick="cancelButtonClicked()">
                        <span class="glyphicon glyphicon-remove"></span> Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>