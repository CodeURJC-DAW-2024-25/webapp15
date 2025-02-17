document.addEventListener('DOMContentLoaded', function() {
    // Sample data - Replace this with actual user data from your backend
    const monthlyData = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
      datasets: [{
        label: 'Money Spent ($)',
        data: [150, 220, 180, 290, 200, 340],
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 2,
        tension: 0.4
      }]
    };
  
    const ctx = document.getElementById('spendingChart').getContext('2d');
    new Chart(ctx, {
      type: 'line',
      data: monthlyData,
      options: {
        responsive: true,
        maintainAspectRatio: false, // Allow the chart to fill the container
        plugins: {
          title: {
            display: true,
            text: 'Your Shopping History'
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Amount ($)'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Month'
            }
          }
        }
      }
    });
  });