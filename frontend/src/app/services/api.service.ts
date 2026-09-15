import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { apiUrl } from '../api-url';
import {
  Booking,
  DashboardStats,
  ParkingSlot,
  ParkingTransaction,
  SlotDetail,
  Vehicle,
  VehicleType
} from '../models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  slots() {
    return this.http.get<ParkingSlot[]>(apiUrl('/api/parking-slots'));
  }

  available(type?: VehicleType) {
    let params = new HttpParams();
    if (type) {
      params = params.set('type', type);
    }
    return this.http.get<ParkingSlot[]>(apiUrl('/api/parking-slots/available'), { params });
  }

  slotDetail(id: number) {
    return this.http.get<SlotDetail>(apiUrl(`/api/parking-slots/${id}/detail`));
  }

  createSlot(body: Partial<ParkingSlot>) {
    return this.http.post<ParkingSlot>(apiUrl('/api/parking-slots'), body);
  }

  updateSlot(id: number, body: Partial<ParkingSlot>) {
    return this.http.put<ParkingSlot>(apiUrl(`/api/parking-slots/${id}`), body);
  }

  deleteSlot(id: number) {
    return this.http.delete(apiUrl(`/api/parking-slots/${id}`));
  }

  vehicles() {
    return this.http.get<Vehicle[]>(apiUrl('/api/vehicles'));
  }

  createVehicle(body: { plateNumber: string; vehicleType: VehicleType; contactPhone?: string; nickname?: string }) {
    return this.http.post<Vehicle>(apiUrl('/api/vehicles'), body);
  }

  updateVehicle(id: number, body: { plateNumber: string; vehicleType: VehicleType; contactPhone?: string; nickname?: string }) {
    return this.http.put<Vehicle>(apiUrl(`/api/vehicles/${id}`), body);
  }

  deleteVehicle(id: number) {
    return this.http.delete(apiUrl(`/api/vehicles/${id}`));
  }

  bookings() {
    return this.http.get<Booking[]>(apiUrl('/api/bookings'));
  }

  createBooking(body: { vehicleId: number; slotId: number; startAt: string; endAt: string }) {
    return this.http.post<Booking>(apiUrl('/api/bookings'), body);
  }

  bulkBook(body: { vehicleIds: number[]; slotIds: number[]; startAt: string; endAt: string }) {
    return this.http.post<Booking[]>(apiUrl('/api/bookings/bulk'), body);
  }

  cancelBooking(id: number) {
    return this.http.put<Booking>(apiUrl(`/api/bookings/${id}/cancel`), {});
  }

  statistics() {
    return this.http.get<DashboardStats>(apiUrl('/api/dashboard/statistics'));
  }

  entry(body: { bookingId?: number; plateNumber?: string; slotNumber?: string }) {
    return this.http.post<ParkingTransaction>(apiUrl('/api/parking/entry'), body);
  }

  exit(body: { transactionId?: number; plateNumber?: string; slotNumber?: string }) {
    return this.http.post<ParkingTransaction>(apiUrl('/api/parking/exit'), body);
  }

  sessions() {
    return this.http.get<ParkingTransaction[]>(apiUrl('/api/parking/sessions'));
  }
}
