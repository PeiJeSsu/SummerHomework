const zones = ['中山', '信義', '仁愛', '中正', '安樂', '七堵', '暖暖'];

$(document).ready(function () {
    // 生成區域按鈕
    zones.forEach(function (zone) {
        const button = `
            <button class="btn btn-primary mx-2 my-2" onclick="loadSights('${zone}')">
                ${zone}區
            </button>`;
        $('#zoneButtons').append(button);
    });
});

function loadSights(zone) {
    $.ajax({
        url: '/SightAPI',
        type: 'GET',
        data: { zone: zone },
        success: function (data) {
            $('#zoneCards').empty(); // 清空之前的卡片內容

            if (data.length === 0) {
                $('#zoneCards').append('<p>沒有找到景點。</p>');
            } else {
                data.forEach(function (sight, index) {
                    let card = `
                        <div class="col-md-4 mb-4">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">${sight.sightName}</h5>
                                    <p class="card-text">區域: ${zone}區</p>
                                    <p class="card-text">分類: ${sight.category}</p>
                                    <a href="https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(sight.address)}" target="_blank" class="btn btn-secondary btn-sm mb-2">地址</a>
                                    <button class="btn btn-info btn-sm mb-2" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                                        詳細資訊
                                    </button>
                                    <div class="collapse" id="collapse${index}">
                                        <div class="card card-body">
                                            <img src="${sight.photoURL}" alt="${sight.sightName}" class="img-fluid mb-2">
                                            <p>${sight.description}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>`;
                    $('#zoneCards').append(card);
                });
            }
        },
        error: function (error) {
            console.log('Error:', error);
        }
    });
}
