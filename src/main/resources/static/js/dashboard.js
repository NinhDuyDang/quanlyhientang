document.addEventListener("DOMContentLoaded", function () {
    // 1. Cấu hình bảng màu đa dạng cho biểu đồ
    const colorPalette = [
        "#0d6efd", "#20c997", "#ffc107", "#dc3545", "#6f42c1",
        "#fd7e14", "#0dcaf0", "#6610f2", "#d63384", "#adb5bd"
    ];

    // ================= 1. BIỂU ĐỒ XU HƯỚNG (LINE CHART) =================
    const lineCanvas = document.getElementById("lineChart");
    if (lineCanvas) {
        new Chart(lineCanvas, {
            type: "line",
            data: {
                labels: ["T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"],
                datasets: [{
                    label: "Số ca bệnh",
                    data: typeof serverLineChartData !== 'undefined' ? serverLineChartData : [],
                    borderColor: "#0d6efd",
                    backgroundColor: "rgba(13, 110, 253, 0.05)",
                    fill: true, tension: 0.3, borderWidth: 2
                }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
        });
    }

    // ================= 2. BIỂU ĐỒ BỆNH VIỆN (BAR CHART) =================
    const barCanvas = document.getElementById("barChart");
    if (barCanvas) {
        const barLabels = typeof serverHospitalLabels !== 'undefined' ? serverHospitalLabels : [];
        const barData = typeof serverHospitalValues !== 'undefined' ? serverHospitalValues : [];

        new Chart(barCanvas, {
            type: "bar",
            data: {
                labels: barLabels,
                datasets: [{
                    label: "Số ca",
                    data: barData,
                    backgroundColor: colorPalette.slice(0, barData.length), // Đa màu sắc
                    borderRadius: 4, barThickness: 25
                }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
        });
    }

    // ================= 3. BIỂU ĐỒ TRẠNG THÁI (PIE CHART) =================
    const pieCanvas = document.getElementById("pieChart");
    if (pieCanvas) {
        const translateLabels = (labels) => labels.map(l => ({
            'BRAIN_DEATH_1': 'Chết não lần 1',
            'BRAIN_DEATH_2': 'Chết não lần 2',
            'BRAIN_DEATH_3': 'Chết não lần 3'
        }[l] || l));

        const rawLabels = typeof serverPieLabels !== 'undefined' ? serverPieLabels : [];
        const pieData = typeof serverPieValues !== 'undefined' ? serverPieValues : [];

        new Chart(pieCanvas, {
            type: "pie",
            data: {
                labels: translateLabels(rawLabels),
                datasets: [{
                    data: pieData,
                    backgroundColor: ["#ffc107", "#dc3545", "#6f42c1"],
                    borderWidth: 2, borderColor: '#ffffff'
                }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
        });
    }
});

// ================= 4. HÀM XỬ LÝ BỘ LỌC TÌM KIẾM =================
function applyFilter() {
    const params = new URLSearchParams({
        hospitalId: document.getElementById("hospital").value,
        status: document.getElementById("status").value,
        fromDate: document.getElementById("fromDate").value,
        toDate: document.getElementById("toDate").value
    });

    fetch(`/api/dashboard/filter?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            // Cập nhật thẻ Card
            document.querySelector(".stat-card h2.text-primary").innerText = data.totalCases || 0;
            document.querySelector(".stat-card h2.text-warning").innerText = data.riskCases || 0;
            document.querySelector(".stat-card h2.text-success").innerText = data.confirmedCases || 0;
            document.querySelector(".stat-card h2.text-danger").innerText = data.notEligibleCases || 0;

            // Cập nhật Bar Chart (Đa màu & Dữ liệu mới)
            const barChart = Chart.getChart("barChart");
            if (barChart) {
                barChart.data.labels = data.hospitalLabels;
                barChart.data.datasets[0].data = data.hospitalValues;
                const colors = ["#0d6efd", "#20c997", "#ffc107", "#dc3545", "#6f42c1", "#fd7e14"];
                barChart.data.datasets[0].backgroundColor = colors.slice(0, data.hospitalValues.length);
                barChart.update();
            }

            // Cập nhật Pie Chart (Nhãn tiếng Việt & Dữ liệu mới)
            const pieChart = Chart.getChart("pieChart");
            if (pieChart) {
                const labelsMap = { 'BRAIN_DEATH_1': 'Chết não lần 1', 'BRAIN_DEATH_2': 'Chết não lần 2', 'BRAIN_DEATH_3': 'Chết não lần 3' };
                const rawKeys = Object.keys(data.statusMap);
                pieChart.data.labels = rawKeys.map(k => labelsMap[k] || k);
                pieChart.data.datasets[0].data = Object.values(data.statusMap);
                pieChart.update();
            }
        })
        .catch(err => console.error("Lỗi:", err));
}
