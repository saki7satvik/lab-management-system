import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { CreateComponentModel } from '../models/create-component.model';
import { Observable, tap } from 'rxjs';
import { ComponentResponseModel } from '../models/component-response.model';
import { TokenService } from '../../../core/services/token';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private http = inject(HttpClient);
  private readonly API = environment.apiUrl; 
  private token = inject(TokenService)


  createComponent(data: CreateComponentModel): Observable<ComponentResponseModel> {
    console.log('InventoryService createComponent called with data:', data);
    return this.http.post<ComponentResponseModel>(
      `${this.API}/components`, data
    ).pipe(
      tap(response => {
        console.log('Create Component Response: ', response);
      })
    );
  }

  getAllComponents(): Observable<ComponentResponseModel[]> {
    console.log('InventoryService getAllComponents called');  
    return this.http.get<ComponentResponseModel[]>(`${this.API}/components`).pipe(
      tap(response => {
        console.log('Get All Components Response: ', response);
      })
    );
  }

  getComponentById(id: string): Observable<ComponentResponseModel> {
    console.log(`InventoryService getComponentById called with id: ${id}`);  
    return this.http.get<ComponentResponseModel>(`${this.API}/components/${id}`).pipe(
      tap(response => {
        console.log('Get Component By ID Response: ', response);
      })
    );
  } 
}
