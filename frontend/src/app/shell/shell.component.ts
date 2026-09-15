import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AuthService } from '../services/auth.service';
import { LoadingService } from '../services/loading.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  template: `
    <mat-toolbar color="primary">
      <span>Smart Parking</span>
      <a mat-button routerLink="/app/layout" routerLinkActive="active">Layout</a>
      @if (staff()) {
        <a mat-button routerLink="/app/dashboard" routerLinkActive="active">Dashboard</a>
        <a mat-button routerLink="/app/gate" routerLinkActive="active">Gate</a>
      }
      @if (admin()) {
        <a mat-button routerLink="/app/slots" routerLinkActive="active">Slots</a>
      }
      <a mat-button routerLink="/app/vehicles" routerLinkActive="active">Vehicles</a>
      <a mat-button routerLink="/app/bookings" routerLinkActive="active">Bookings</a>
      <a mat-button routerLink="/app/bulk" routerLinkActive="active">Bulk</a>
      <span class="spacer"></span>
      <span class="who">{{ auth.user()?.fullName }}</span>
      <button mat-button (click)="auth.logout()">Logout</button>
    </mat-toolbar>
    @if (loading.active()) {
      <mat-progress-bar mode="indeterminate" />
    }
    <router-outlet />
  `,
  styles: [`
    .spacer { flex: 1; }
    .who { font-size: 14px; opacity: .9; margin-right: 8px; }
    a.active { background: rgb(255 255 255 / 16%); }
  `]
})
export class ShellComponent {
  readonly auth = inject(AuthService);
  readonly loading = inject(LoadingService);
  readonly staff = computed(() => this.auth.isStaff());
  readonly admin = computed(() => this.auth.hasRole('ADMIN'));
}
