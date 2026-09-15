import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, inject } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { ApiService } from '../../services/api.service';
import { DashboardStats } from '../../models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <main class="app-page">
      <h1>Admin dashboard</h1>
      @if (stats) {
        <section class="stats">
          <div class="stat-card">Total slots <strong>{{ stats.totalSlots }}</strong></div>
          <div class="stat-card">Vacant <strong>{{ stats.vacant }}</strong></div>
          <div class="stat-card">Occupied <strong>{{ stats.occupied }}</strong></div>
          <div class="stat-card">Pre-booked <strong>{{ stats.preBooked }}</strong></div>
          <div class="stat-card">Reserved <strong>{{ stats.reserved }}</strong></div>
          <div class="stat-card">Parked now <strong>{{ stats.currentlyParked }}</strong></div>
        </section>
        <p>Utilization {{ stats.utilizationPercent }}%</p>
        <div class="chart-wrap"><canvas #util></canvas></div>
        <div class="chart-wrap"><canvas #types></canvas></div>
        <div class="chart-wrap"><canvas #daily></canvas></div>
        <div class="chart-wrap"><canvas #monthly></canvas></div>
      }
    </main>
  `
})
export class DashboardComponent implements AfterViewInit, OnDestroy {
  private api = inject(ApiService);
  stats?: DashboardStats;
  @ViewChild('util') util?: ElementRef<HTMLCanvasElement>;
  @ViewChild('types') types?: ElementRef<HTMLCanvasElement>;
  @ViewChild('daily') daily?: ElementRef<HTMLCanvasElement>;
  @ViewChild('monthly') monthly?: ElementRef<HTMLCanvasElement>;
  private charts: Chart[] = [];

  ngAfterViewInit(): void {
    this.api.statistics().subscribe(stats => {
      this.stats = stats;
      queueMicrotask(() => this.draw(stats));
    });
  }

  ngOnDestroy(): void {
    this.charts.forEach(c => c.destroy());
  }

  private draw(stats: DashboardStats): void {
    const occupied = stats.occupied;
    const free = Math.max(0, stats.totalSlots - occupied);
    if (this.util) {
      this.charts.push(new Chart(this.util.nativeElement, {
        type: 'doughnut',
        data: {
          labels: ['Occupied', 'Other'],
          datasets: [{ data: [occupied, free], backgroundColor: ['#ef4444', '#14b8a6'] }]
        },
        options: { plugins: { title: { display: true, text: 'Parking utilization' } } }
      }));
    }
    if (this.types) {
      const labels = Object.keys(stats.vehicleTypeDistribution);
      this.charts.push(new Chart(this.types.nativeElement, {
        type: 'bar',
        data: {
          labels,
          datasets: [{
            label: 'Slots by type',
            data: labels.map(k => stats.vehicleTypeDistribution[k]),
            backgroundColor: '#0f766e'
          }]
        },
        options: { plugins: { title: { display: true, text: 'Vehicle type distribution' } } }
      }));
    }
    if (this.daily) {
      this.charts.push(new Chart(this.daily.nativeElement, {
        type: 'line',
        data: {
          labels: stats.dailyBookings.map(x => x.label),
          datasets: [{ label: 'Daily bookings', data: stats.dailyBookings.map(x => x.count), borderColor: '#0f766e' }]
        }
      }));
    }
    if (this.monthly) {
      this.charts.push(new Chart(this.monthly.nativeElement, {
        type: 'bar',
        data: {
          labels: stats.monthlyBookings.map(x => x.label),
          datasets: [{ label: 'Monthly bookings', data: stats.monthlyBookings.map(x => x.count), backgroundColor: '#115e59' }]
        }
      }));
    }
  }
}
