import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../services/api.service';
import { ParkingTransaction } from '../../models';

@Component({
  selector: 'app-gate',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, MatFormFieldModule, MatInputModule, MatButtonModule, MatTableModule],
  template: `
    <main class="app-page">
      <h1>Vehicle entry / exit</h1>
      <form [formGroup]="form" class="form-grid">
        <mat-form-field appearance="outline">
          <mat-label>Booking ID</mat-label>
          <input matInput type="number" formControlName="bookingId" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Plate</mat-label>
          <input matInput formControlName="plateNumber" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Slot number</mat-label>
          <input matInput formControlName="slotNumber" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Transaction ID (exit)</mat-label>
          <input matInput type="number" formControlName="transactionId" />
        </mat-form-field>
        <button mat-flat-button color="primary" type="button" (click)="enter()">Entry</button>
        <button mat-stroked-button type="button" (click)="leave()">Exit</button>
      </form>
      <h2>Open sessions</h2>
      <table mat-table [dataSource]="sessions" class="mat-elevation-z1" style="width:100%">
        <ng-container matColumnDef="id"><th mat-header-cell *matHeaderCellDef>Tx</th><td mat-cell *matCellDef="let r">{{ r.id }}</td></ng-container>
        <ng-container matColumnDef="plateNumber"><th mat-header-cell *matHeaderCellDef>Plate</th><td mat-cell *matCellDef="let r">{{ r.plateNumber }}</td></ng-container>
        <ng-container matColumnDef="slotNumber"><th mat-header-cell *matHeaderCellDef>Slot</th><td mat-cell *matCellDef="let r">{{ r.slotNumber }}</td></ng-container>
        <ng-container matColumnDef="entryTime"><th mat-header-cell *matHeaderCellDef>Entered</th>
          <td mat-cell *matCellDef="let r">{{ r.entryTime | date:'short' }}</td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </main>
  `
})
export class GateComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  cols = ['id', 'plateNumber', 'slotNumber', 'entryTime'];
  sessions: ParkingTransaction[] = [];
  form = this.fb.nonNullable.group({
    bookingId: [''],
    plateNumber: [''],
    slotNumber: [''],
    transactionId: ['']
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.sessions().subscribe(s => this.sessions = s);
  }

  enter(): void {
    const v = this.form.getRawValue();
    this.api.entry({
      bookingId: v.bookingId ? Number(v.bookingId) : undefined,
      plateNumber: v.plateNumber || undefined,
      slotNumber: v.slotNumber || undefined
    }).subscribe(() => this.reload());
  }

  leave(): void {
    const v = this.form.getRawValue();
    this.api.exit({
      transactionId: v.transactionId ? Number(v.transactionId) : undefined,
      plateNumber: v.plateNumber || undefined,
      slotNumber: v.slotNumber || undefined
    }).subscribe(() => this.reload());
  }
}
