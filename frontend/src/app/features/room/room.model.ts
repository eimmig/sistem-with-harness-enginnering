export type RoomStatus = 'AVAILABLE' | 'DIRTY' | 'OCCUPIED';

export const ROOM_STATUS_LABELS: Record<RoomStatus, string> = {
  AVAILABLE: 'Disponível',
  DIRTY: 'Sujo',
  OCCUPIED: 'Ocupado'
};

export const ROOM_STATUSES: RoomStatus[] = ['AVAILABLE', 'DIRTY', 'OCCUPIED'];

export interface Room {
  id: number;
  number: string;
  status: RoomStatus;
  category: {
    id: number;
    name: string;
  };
}

export interface RoomRequest {
  number: string;
  roomCategoryId: number;
}

export interface RoomStatusRequest {
  status: RoomStatus;
}
