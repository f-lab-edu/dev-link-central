<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Page Title</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap JS 및 jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"
            integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
            crossorigin="anonymous">
    </script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            padding-top: 30px;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            height: 100vh;
        }

        .container {
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            width: 100%;
            max-width: 800px;
            margin-top: 40px;
        }

        .feed-container {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
        }

        .feed-item {
            background: #fff;
            padding: 15px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: calc(30% - 20px);
            text-align: center;
            cursor: pointer;
            transition: transform 0.3s;
            margin: 5px;
            word-wrap: break-word;
            overflow-wrap: break-word;
        }

        .feed-item:hover {
            transform: scale(1.05);
        }

        .feed-title {
            font-size: 1.2em;
            font-weight: bold;
        }

        .loading-spinner {
            text-align: center;
            margin-top: 20px;
        }

        .title {
            text-align: center;
            margin-bottom: 20px;
            font-size: 27px;
            font-weight: bold;
            color: #007bff;
        }
    </style>

    <script>
        $(document).ready(function() {
            loadMyFeeds();
        });

        function loadMyFeeds() {
            $('#feedContainer').empty(); // 기존 피드 지우기
            $.ajax({
                url: '/api/v1/group-feed/my-feeds',
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwt")
                },
                success: function(response) {
                    const feeds = response.feeds;
                    if (feeds.length > 0) {
                        feeds.forEach(feed => {
                            const feedHtml =
                                '<div class="feed-item" onclick="viewFeedDetail(' + feed.id + ')">' +
                                '<div class="feed-title">' + feed.title + '</div>' +
                                '</div>';
                            $('#feedContainer').append(feedHtml);
                        });
                    } else {
                        $('#feedContainer').append('<p>No feeds available</p>');
                    }
                },
                error: function(xhr) {
                    console.error('Failed to load feeds:', xhr.responseText);
                }
            });
        }

        function viewFeedDetail(feedId) {
            window.location.href = "/api/v1/view/group-feed/detail/" + feedId;
        }
    </script>
</head>
<body>
<div class="container">
    <div class="title">나의 피드</div>
    <div class="feed-container" id="feedContainer"></div>
    <div class="loading-spinner" id="loadingSpinner" style="display: none;">
        <div class="spinner-border text-primary" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
</div>
</body>
</html>