import { ItemCondition, OverallCondition, ReturnStatus } from './return-enums.model';

export interface ReturnItemResponseModel {
  id: string;
  requestItemId: string;
  componentId: string;
  quantityReturned: number;
  condition: ItemCondition | string;
  damageRemarks: string;
}

export interface ReturnResponseModel {
  id: string;
  requestId: string;
  returnedBy: string;
  inspectedBy: string;
  status: ReturnStatus | string;
  overallCondition: OverallCondition | string;
  fineGenerated: boolean;
  remarks: string;
  createdAt: string; // ISO Date string from Java LocalDateTime
  inspectedAt: string; // ISO Date string from Java LocalDateTime
  items: ReturnItemResponseModel[];
}