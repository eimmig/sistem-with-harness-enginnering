export interface Reservation {
  id: number;
  guest: { id: number; name: string };
  room: { id: number; number: string };
  expectedCheckIn: string;
  expectedCheckOut: string;
  parkingRequested: boolean;
  actualCheckIn: string | null;
}

export interface ReservationRequest {
  guestId: number;
  roomId: number;
  expectedCheckIn: string;
  expectedCheckOut: string;
  parkingRequested: boolean;
}
