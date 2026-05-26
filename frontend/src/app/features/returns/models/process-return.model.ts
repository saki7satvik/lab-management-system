import { ItemCondition } from './return-enums.model';

export interface ProcessReturnItemModel {
  requestItemId: string;
  quantityReturned: number;
  condition: ItemCondition | string;
  damageRemarks?: string; // Optional because of backend mapping
}

export interface ProcessReturnRequestModel {
  requestId: string;
  items: ProcessReturnItemModel[];
  remarks?: string; // Optional overall remarks
}