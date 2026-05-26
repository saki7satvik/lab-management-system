import { RequestItemModel } from "./request-item.model";

export interface CreateRequestModel {
    targetType: String;
    targetId: String;  
    items: RequestItemModel[];
}