import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../services/api.service';
import { ParkingSlot, SlotStatus, VehicleType } from '../../models';

@Component({
  selector: 'app-slots',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatTableModule],
  template: `
    <main class="app-page">
      <h1>Manage slots</h1>
      <form [formGroup]="form" (ngSubmit)="add()" class="form-grid">
        <mat-form-field appearance="outline"><mat-label>Number</mat-label><input matInput formControlName="slotNumber" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Area</mat-label><input matInput formControlName="area" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Floor</mat-label><input matInput formControlName="floor" /></mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Type</mat-label>
          <mat-select formControlName="vehicleType">
            @for (t of types; track t) { <mat-option [value]="t">{{ t }}</mat-option> }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            @for (s of statuses; track s) { <mat-option [value]="s">{{ s }}</mat-option> }
          </mat-select>
        </mat-form-field>
        <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid">Add slot</button>
      </form>
      <table mat-table [dataSource]="rows" class="mat-elevation-z1" style="width:100%;margin-top:16px">
        <ng-container matColumnDef="slotNumber"><th mat-header-cell *matHeaderCellDef>Slot</th><td mat-cell *matCellDef="let r">{{ r.slotNumber }}</td></ng-container>
        <ng-container matColumnDef="area"><th mat-header-cell *matHeaderCellDef>Area</th><td mat-cell *matCellDef="let r">{{ r.area }} / {{ r.floor }}</td></ng-container>
        <ng-container matColumnDef="vehicleType"><th mat-header-cell *matHeaderCellDef>Type</th><td mat-cell *matCellDef="let r">{{ r.vehicleType }}</td></ng-container>
        <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Status</th><td mat-cell *matCellDef="let r">{{ r.status }}</td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </main>
  `
})
export class SlotsComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  types: VehicleType[] = ['CAR', 'BIKE', 'EV', 'OTHER'];
  statuses: SlotStatus[] = ['VACANT', 'OCCUPIED', 'RESERVED', 'PRE_BOOKED', 'OUT_OF_SERVICE'];
  cols = ['slotNumber', 'area', 'vehicleType', 'status'];
  rows: ParkingSlot[] = [];
  form = this.fb.nonNullable.group({
    slotNumber: ['', Validators.required],
    area: ['Car Park A', Validators.required],
    floor: ['L1', Validators.required],
    vehicleType: ['CAR' as VehicleType, Validators.required],
    status: ['VACANT' as SlotStatus]
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.slots().subscribe(s => this.rows = s);
  }

  add(): void {
    this.api.createSlot(this.form.getRawValue()).subscribe(() => this.reload());
  }
}
