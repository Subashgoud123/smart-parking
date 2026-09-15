import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../services/api.service';
import { Booking } from '../../models';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [DatePipe, MatTableModule, MatButtonModule],
  template: `
    <main class="app-page">
      <h1>Bookings</h1>
      <table mat-table [dataSource]="rows" class="mat-elevation-z1" style="width:100%">
        <ng-container matColumnDef="id"><th mat-header-cell *matHeaderCellDef>ID</th><td mat-cell *matCellDef="let r">{{ r.id }}</td></ng-container>
        <ng-container matColumnDef="slotNumber"><th mat-header-cell *matHeaderCellDef>Slot</th><td mat-cell *matCellDef="let r">{{ r.slotNumber }}</td></ng-container>
        <ng-container matColumnDef="plateNumber"><th mat-header-cell *matHeaderCellDef>Vehicle</th><td mat-cell *matCellDef="let r">{{ r.plateNumber }}</td></ng-container>
        <ng-container matColumnDef="window"><th mat-header-cell *matHeaderCellDef>Window</th>
          <td mat-cell *matCellDef="let r">{{ r.startAt | date:'short' }} – {{ r.endAt | date:'short' }}</td>
        </ng-container>
        <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Status</th><td mat-cell *matCellDef="let r">{{ r.status }}</td></ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let r">
            @if (r.status !== 'CANCELLED' && r.status !== 'COMPLETED' && r.status !== 'ACTIVE') {
              <button mat-button color="warn" (click)="cancel(r)">Cancel</button>
            }
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </main>
  `
})
export class BookingsComponent implements OnInit {
  private api = inject(ApiService);
  cols = ['id', 'slotNumber', 'plateNumber', 'window', 'status', 'actions'];
  rows: Booking[] = [];

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.bookings().subscribe(b => this.rows = b);
  }

  cancel(b: Booking): void {
    this.api.cancelBooking(b.id).subscribe(() => this.reload());
  }
}
