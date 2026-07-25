export interface Guest {
  id: number;
  name: string;
  document: string;
  phone: string;
}

export interface GuestRequest {
  name: string;
  document: string;
  phone: string;
}

export interface GuestSearchFilter {
  name?: string;
  document?: string;
  phone?: string;
}
