document.addEventListener("DOMContentLoaded", function () {

    // ================= 1. BIỂU ĐỒ XU HƯỚNG (LINE CHART) =================
    const lineCanvas = document.getElementById("lineChart");
    if (lineCanvas) {
        const lineLabels = ["T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"];
        const lineData = typeof serverLineChartData !== 'undefined' ? serverLineChartData : [];

        new Chart(lineCanvas, {
            type: "line",
            data: {
                labels: lineLabels,
                datasets: [{
                    label: "Số ca bệnh",
                    data: lineData,
                    borderColor: "#0d6efd",
                    backgroundColor: "rgba(13, 110, 253, 0.05)",
                    fill: true,
                    tension: 0.3,
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: { beginAtZero: true, grid: { color: '#f1f5f9' } },
                    x: { grid: { display: false } }
                }
            }
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
                    backgroundColor: "#20c997",
                    borderRadius: 4,
                    barThickness: 25
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: { beginAtZero: true, grid: { color: '#f1f5f9' } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    // ================= 3. BIỂU ĐỒ TRẠNG THÁI (PIE CHART) =================
    const pieCanvas = document.getElementById("pieChart");
    if (pieCanvas) {
        const translateLabels = (labels) => {
            return labels.map(label => {
                if (label === 'BRAIN_DEATH_1') return 'Chết não lần 1 ';
                if (label === 'BRAIN_DEATH_2') return 'Chết não lần 2';
                if (label === 'BRAIN_DEATH_3') return 'Chết não lần 3';
                return label;
            });
        };

        const rawLabels = typeof serverPieLabels !== 'undefined' ? serverPieLabels : [];
        const vietnameseLabels = translateLabels(rawLabels);
        const pieData = typeof serverPieValues !== 'undefined' ? serverPieValues : [];

        new Chart(pieCanvas, {
            type: "pie",
            data: {
                labels: vietnameseLabels,
                datasets: [{
                    data: pieData,
                    backgroundColor: ["#ffc107", "#198754", "#dc3545"],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: { boxWidth: 15, font: { size: 12 } }
                    }
                }
            }
        });
    }
});

// ================= 4. HÀM XỬ LÝ BỘ LỌC TÌM KIẾM =================
// Thay thế hàm applyFilter() cũ bằng hàm tương tác API thời gian thực này:
function applyFilter() {
    // 1. Thu thập dữ liệu từ các ô nhập bộ lọc trên giao diện
    const hospitalId = document.getElementById("hospital").value;
    const status = document.getElementById("status").value;
    const fromDate = document.getElementById("fromDate").value;
    const toDate = document.getElementById("toDate").value;

    // 2. Tạo chuỗi URL chứa tham số lọc động
    let url = `/api/dashboard/filter?`;
    if (hospitalId !== 'all') url += `hospitalId=${hospitalId}&`;
    if (status !== 'all') url += `status=${status}&`;
    if (fromDate) url += `fromDate=${fromDate}&`;
    if (toDate) url += `toDate=${toDate}`;

    // 3. Gửi yêu cầu ngầm AJAX lên hệ thống Backend
    fetch(url)
        .then(response => response.json())
        .then(data => {
            // A. Cập nhật số hiển thị trực tiếp trên 4 Thẻ Card đầu trang bằng JS
            document.querySelector(".stat-card h2.text-primary").innerText = data.totalCases;
            document.querySelector(".stat-card h2.text-warning").innerText = data.riskCases;
            document.querySelector(".stat-card h2.text-success").innerText = data.confirmedCases;
            document.querySelector(".stat-card h2.text-danger").innerText = data.notEligibleCases;

            // B. Vẽ lại Biểu đồ Cột Bệnh viện (Bar Chart) với dữ liệu mới
            const barChartInstance = Chart.getChart("barChart");
            if (barChartInstance) {
                barChartInstance.data.labels = data.hospitalLabels;
                barChartInstance.data.datasets[0].data = data.hospitalValues;
                barChartInstance.update(); // Lệnh cập nhật làm mới biểu đồ
            }

            // C. Vẽ lại Biểu đồ Tròn Trạng thái (Pie Chart) với dữ liệu mới
            const pieChartInstance = Chart.getChart("pieChart");
            if (pieChartInstance) {
                const translateLabels = (labels) => labels.map(l => {
                    if (l === 'BRAIN_DEATH_1') return 'Chết não lần 1 ';
                    if (l === 'BRAIN_DEATH_2') return 'Chết não lần 2';
                    if (l === 'BRAIN_DEATH_3') return 'Chết não lần 3';
                    return l;
                });
                pieChartInstance.data.labels = translateLabels(Object.keys(data.statusMap));
                pieChartInstance.data.datasets[0].data = Object.values(data.statusMap);
                pieChartInstance.update(); // Lệnh cập nhật làm mới biểu đồ
            }
        })
        .catch(error => console.error("Lỗi hệ thống khi lọc dữ liệu:", error));
}
