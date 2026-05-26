import { RequestItemResponseModel } from "./request-item-response.model";

export interface RequestResponseModel{
    id: string;
    createdBy: string;
    targetType: string;
    targetId: string;
    status: string;
    createdAt: Date;
    updatedAt: Date;
    items: RequestItemResponseModel[];
}