export const DAYS_OF_WEEK = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY'
] as const;

export type DayOfWeek = (typeof DAYS_OF_WEEK)[number];

export const DAY_OF_WEEK_LABELS: Record<DayOfWeek, string> = {
  MONDAY: 'Segunda-feira',
  TUESDAY: 'Terça-feira',
  WEDNESDAY: 'Quarta-feira',
  THURSDAY: 'Quinta-feira',
  FRIDAY: 'Sexta-feira',
  SATURDAY: 'Sábado',
  SUNDAY: 'Domingo'
};

export interface RoomCategory {
  id: number;
  name: string;
  prices: Partial<Record<DayOfWeek, number>>;
}

export interface RoomCategoryRequest {
  name: string;
}

export interface RoomCategoryPricesRequest {
  prices: Record<DayOfWeek, number>;
}
