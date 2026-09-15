export type Role = 'ADMIN' | 'CUSTOMER' | 'STAFF';
export type VehicleType = 'CAR' | 'BIKE' | 'EV' | 'OTHER';
export type SlotStatus = 'VACANT' | 'OCCUPIED' | 'RESERVED' | 'PRE_BOOKED' | 'OUT_OF_SERVICE';
export type BookingStatus = 'CONFIRMED' | 'PRE_BOOKED' | 'ACTIVE' | 'CANCELLED' | 'COMPLETED';

export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  active: boolean;
  roles: Role[];
}

export interface TokenResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface ParkingSlot {
  id: number;
  slotNumber: string;
  area: string;
  floor: string;
  vehicleType: VehicleType;
  status: SlotStatus;
  notes?: string;
}

export interface Vehicle {
  id: number;
  plateNumber: string;
  vehicleType: VehicleType;
  ownerId: number;
  ownerName: string;
  contactPhone?: string;
  nickname?: string;
}

export interface Booking {
  id: number;
  userId: number;
  userName: string;
  vehicleId: number;
  plateNumber: string;
  slotId: number;
  slotNumber: string;
  startAt: string;
  endAt: string;
  status: BookingStatus;
  bulkGroupId?: string;
}

export interface ParkingTransaction {
  id: number;
  bookingId?: number;
  slotId: number;
  slotNumber: string;
  vehicleId: number;
  plateNumber: string;
  entryTime: string;
  exitTime?: string;
  durationMinutes?: number;
}

export interface SlotDetail {
  slot: ParkingSlot;
  activeBooking?: Booking;
  openTransaction?: ParkingTransaction;
}

export interface NamedCount {
  label: string;
  count: number;
}

export interface DashboardStats {
  totalSlots: number;
  vacant: number;
  occupied: number;
  reserved: number;
  preBooked: number;
  outOfService: number;
  currentlyParked: number;
  utilizationPercent: number;
  vehicleTypeDistribution: Record<string, number>;
  dailyBookings: NamedCount[];
  monthlyBookings: NamedCount[];
}

export interface ApiError {
  code: string;
  message: string;
}
