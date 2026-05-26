import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { CreateRequestModel } from '../models/request/create-request.model';
import { Observable, tap } from 'rxjs';
import { RequestResponseModel } from '../models/response/request-response.model';

@Injectable({
  providedIn: 'root',
})
export class RequestService {
  private http = inject(HttpClient);
  private readonly API = environment.apiUrl;

  createRequest(requestData: CreateRequestModel): Observable<RequestResponseModel>{
    console.log('RequestService createRequest called with data:', requestData); 
    return this.http.post<RequestResponseModel>(
      `${this.API}/requests`, requestData
    ).pipe(
      tap(response => {
        console.log('Create Request Response: ', response);
      })
    );
  }

  getAllRequests(): Observable<RequestResponseModel[]> {
    console.log('RequestService getAllRequests called');
    return this.http.get<RequestResponseModel[]>(
      `${this.API}/requests`
    ).pipe(
      tap(requests => {
        console.log('Get All Requests Response: ', requests);
      })
    );
  }

  getRequestById(id: string): Observable<RequestResponseModel> {  
    console.log('RequestService getRequestById called with ID: ', id);
    return this.http.get<RequestResponseModel>(
      `${this.API}/requests/${id}`
    ).pipe(
      tap(request => {
        console.log('Get Request by ID Response: ', request);
      })
    );
  }

  getMyRequests(): Observable<RequestResponseModel[]> {
    console.log('RequestService getMyRequests called');
    return this.http.get<RequestResponseModel[]>(
      `${this.API}/requests/my`
    ).pipe(
      tap(requests => {
        console.log('Get My Requests Response: ', requests);
      })
    );  
  }

  getRequestsByUserId(collegeId: string): Observable<RequestResponseModel[]> {
    console.log('RequestService getRequestsByUserId called with College ID: ', collegeId);
    return this.http.get<RequestResponseModel[]>(
      `${this.API}/requests/user/${collegeId}`
    ).pipe(
      tap(requests => {
        console.log('Get Requests by User ID Response: ', requests);
      })
    );
  }

}