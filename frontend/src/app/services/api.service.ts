import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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
    return this.http.get<ParkingSlot[]>('/api/parking-slots');
  }

  available(type?: VehicleType) {
    let params = new HttpParams();
    if (type) {
      params = params.set('type', type);
    }
    return this.http.get<ParkingSlot[]>('/api/parking-slots/available', { params });
  }

  slotDetail(id: number) {
    return this.http.get<SlotDetail>(`/api/parking-slots/${id}/detail`);
  }

  createSlot(body: Partial<ParkingSlot>) {
    return this.http.post<ParkingSlot>('/api/parking-slots', body);
  }

  updateSlot(id: number, body: Partial<ParkingSlot>) {
    return this.http.put<ParkingSlot>(`/api/parking-slots/${id}`, body);
  }

  deleteSlot(id: number) {
    return this.http.delete(`/api/parking-slots/${id}`);
  }

  vehicles() {
    return this.http.get<Vehicle[]>('/api/vehicles');
  }

  createVehicle(body: { plateNumber: string; vehicleType: VehicleType; contactPhone?: string; nickname?: string }) {
    return this.http.post<Vehicle>('/api/vehicles', body);
  }

  updateVehicle(id: number, body: { plateNumber: string; vehicleType: VehicleType; contactPhone?: string; nickname?: string }) {
    return this.http.put<Vehicle>(`/api/vehicles/${id}`, body);
  }

  deleteVehicle(id: number) {
    return this.http.delete(`/api/vehicles/${id}`);
  }

  bookings() {
    return this.http.get<Booking[]>('/api/bookings');
  }

  createBooking(body: { vehicleId: number; slotId: number; startAt: string; endAt: string }) {
    return this.http.post<Booking>('/api/bookings', body);
  }

  bulkBook(body: { vehicleIds: number[]; slotIds: number[]; startAt: string; endAt: string }) {
    return this.http.post<Booking[]>('/api/bookings/bulk', body);
  }

  cancelBooking(id: number) {
    return this.http.put<Booking>(`/api/bookings/${id}/cancel`, {});
  }

  statistics() {
    return this.http.get<DashboardStats>('/api/dashboard/statistics');
  }

  entry(body: { bookingId?: number; plateNumber?: string; slotNumber?: string }) {
    return this.http.post<ParkingTransaction>('/api/parking/entry', body);
  }

  exit(body: { transactionId?: number; plateNumber?: string; slotNumber?: string }) {
    return this.http.post<ParkingTransaction>('/api/parking/exit', body);
  }

  sessions() {
    return this.http.get<ParkingTransaction[]>('/api/parking/sessions');
  }
}
