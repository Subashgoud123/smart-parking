import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../services/api.service';
import { Vehicle, VehicleType } from '../../models';

@Component({
  selector: 'app-vehicles',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatTableModule],
  template: `
    <main class="app-page">
      <h1>Vehicles</h1>
      <form [formGroup]="form" (ngSubmit)="add()" class="form-grid">
        <mat-form-field appearance="outline">
          <mat-label>Plate</mat-label>
          <input matInput formControlName="plateNumber" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Type</mat-label>
          <mat-select formControlName="vehicleType">
            @for (t of types; track t) { <mat-option [value]="t">{{ t }}</mat-option> }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Nickname</mat-label>
          <input matInput formControlName="nickname" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Contact</mat-label>
          <input matInput formControlName="contactPhone" />
        </mat-form-field>
        <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid">{{ editing ? 'Save vehicle' : 'Register vehicle' }}</button>
        @if (editing) {
          <button mat-button type="button" (click)="cancelEdit()">Cancel</button>
        }
      </form>
      <table mat-table [dataSource]="rows" class="mat-elevation-z1" style="width:100%;margin-top:20px">
        <ng-container matColumnDef="plateNumber"><th mat-header-cell *matHeaderCellDef>Plate</th><td mat-cell *matCellDef="let r">{{ r.plateNumber }}</td></ng-container>
        <ng-container matColumnDef="vehicleType"><th mat-header-cell *matHeaderCellDef>Type</th><td mat-cell *matCellDef="let r">{{ r.vehicleType }}</td></ng-container>
        <ng-container matColumnDef="ownerName"><th mat-header-cell *matHeaderCellDef>Owner</th><td mat-cell *matCellDef="let r">{{ r.ownerName }}</td></ng-container>
        <ng-container matColumnDef="nickname"><th mat-header-cell *matHeaderCellDef>Name</th><td mat-cell *matCellDef="let r">{{ r.nickname }}</td></ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let r">
            <button mat-button (click)="edit(r)">Edit</button>
            <button mat-button color="warn" (click)="remove(r)">Delete</button>
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </main>
  `
})
export class VehiclesComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  types: VehicleType[] = ['CAR', 'BIKE', 'EV', 'OTHER'];
  cols = ['plateNumber', 'vehicleType', 'ownerName', 'nickname', 'actions'];
  rows: Vehicle[] = [];
  editing?: Vehicle;
  form = this.fb.nonNullable.group({
    plateNumber: ['', Validators.required],
    vehicleType: ['CAR' as VehicleType, Validators.required],
    nickname: [''],
    contactPhone: ['']
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.vehicles().subscribe(v => this.rows = v);
  }

  add(): void {
    const body = this.form.getRawValue();
    const req = this.editing
      ? this.api.updateVehicle(this.editing.id, body)
      : this.api.createVehicle(body);
    req.subscribe(() => {
      this.cancelEdit();
      this.reload();
    });
  }

  edit(v: Vehicle): void {
    this.editing = v;
    this.form.patchValue({
      plateNumber: v.plateNumber,
      vehicleType: v.vehicleType,
      nickname: v.nickname || '',
      contactPhone: v.contactPhone || ''
    });
  }

  cancelEdit(): void {
    this.editing = undefined;
    this.form.reset({ plateNumber: '', vehicleType: 'CAR', nickname: '', contactPhone: '' });
  }

  remove(v: Vehicle): void {
    this.api.deleteVehicle(v.id).subscribe(() => this.reload());
  }
}
