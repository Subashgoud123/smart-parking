import { Component, OnInit, inject } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ParkingSlot, SlotDetail, Vehicle, VehicleType } from '../../models';
import { localDateTimeValue, toApiDateTime } from '../../util/datetime';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    NgClass, DatePipe, ReactiveFormsModule, MatButtonModule, MatFormFieldModule,
    MatSelectModule, MatInputModule, MatProgressBarModule
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  loading = true;
  slots: ParkingSlot[] = [];
  vehicles: Vehicle[] = [];
  selected?: SlotDetail;
  typeFilter: VehicleType | 'ALL' = 'ALL';
  vacantOnly = false;
  bookForm = this.fb.nonNullable.group({
    vehicleId: [0, Validators.required],
    startAt: [localDateTimeValue(0), Validators.required],
    endAt: [localDateTimeValue(2), Validators.required]
  });

  ngOnInit(): void {
    this.refresh();
  }

  areas(): string[] {
    return [...new Set(this.slots.map(s => s.area))].filter(a => this.inArea(a).length > 0);
  }

  inArea(area: string): ParkingSlot[] {
    return this.slots.filter(s =>
      s.area === area
      && (this.typeFilter === 'ALL' || s.vehicleType === this.typeFilter)
      && (!this.vacantOnly || s.status === 'VACANT'));
  }

  refresh(): void {
    this.loading = true;
    forkJoin({ slots: this.api.slots(), vehicles: this.api.vehicles() }).subscribe({
      next: ({ slots, vehicles }) => {
        this.slots = slots;
        this.vehicles = vehicles;
        const first = vehicles[0];
        if (first) {
          this.bookForm.patchValue({ vehicleId: first.id });
        }
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  open(slot: ParkingSlot): void {
    this.api.slotDetail(slot.id).subscribe(detail => this.selected = detail);
  }

  book(): void {
    const slot = this.selected?.slot;
    if (!slot || this.bookForm.invalid) {
      return;
    }
    const v = this.bookForm.getRawValue();
    this.api.createBooking({
      vehicleId: Number(v.vehicleId),
      slotId: slot.id,
      startAt: toApiDateTime(v.startAt),
      endAt: toApiDateTime(v.endAt)
    }).subscribe(() => {
      this.selected = undefined;
      this.refresh();
    });
  }
}
