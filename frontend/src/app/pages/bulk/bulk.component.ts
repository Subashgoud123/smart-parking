import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ParkingSlot, Vehicle } from '../../models';
import { localDateTimeValue, toApiDateTime } from '../../util/datetime';

@Component({
  selector: 'app-bulk',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatSelectModule, MatInputModule, MatButtonModule],
  template: `
    <main class="app-page">
      <h1>Bulk booking</h1>
      <p>Select the same number of vehicles and slots. Availability is checked before confirm.</p>
      <form [formGroup]="form" (ngSubmit)="submit()" class="form-grid">
        <mat-form-field appearance="outline">
          <mat-label>Vehicles</mat-label>
          <mat-select formControlName="vehicleIds" multiple>
            @for (v of vehicles; track v.id) {
              <mat-option [value]="v.id">{{ v.plateNumber }} ({{ v.vehicleType }})</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Slots</mat-label>
          <mat-select formControlName="slotIds" multiple>
            @for (s of slots; track s.id) {
              <mat-option [value]="s.id">{{ s.slotNumber }} · {{ s.vehicleType }} · {{ s.status }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Start</mat-label>
          <input matInput type="datetime-local" formControlName="startAt" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>End</mat-label>
          <input matInput type="datetime-local" formControlName="endAt" />
        </mat-form-field>
        <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid">Confirm bulk booking</button>
      </form>
      @if (created) { <p>Created {{ created }} bookings.</p> }
    </main>
  `
})
export class BulkComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  vehicles: Vehicle[] = [];
  slots: ParkingSlot[] = [];
  created?: number;
  form = this.fb.nonNullable.group({
    vehicleIds: [[] as number[], Validators.required],
    slotIds: [[] as number[], Validators.required],
    startAt: [localDateTimeValue(1), Validators.required],
    endAt: [localDateTimeValue(5), Validators.required]
  });

  ngOnInit(): void {
    forkJoin({ v: this.api.vehicles(), s: this.api.slots() }).subscribe(({ v, s }) => {
      this.vehicles = v;
      this.slots = s.filter(x => x.status !== 'OUT_OF_SERVICE' && x.status !== 'OCCUPIED');
    });
  }

  submit(): void {
    const v = this.form.getRawValue();
    this.api.bulkBook({
      vehicleIds: v.vehicleIds,
      slotIds: v.slotIds,
      startAt: toApiDateTime(v.startAt),
      endAt: toApiDateTime(v.endAt)
    }).subscribe(list => this.created = list.length);
  }
}
