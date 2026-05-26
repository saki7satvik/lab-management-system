import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ReturnResponseModel } from '../models/return-response.model';
import { ProcessReturnRequestModel } from '../models/process-return.model';

@Injectable({
  providedIn: 'root'
})
export class ReturnService {
  private http = inject(HttpClient);
  private readonly API = `${environment.apiUrl}/returns`;

  // 1. PROCESS RETURN
  processReturn(returnPayload: ProcessReturnRequestModel): Observable<ReturnResponseModel> {
    console.log('ReturnService processReturn called with data:', returnPayload);
    return this.http.post<ReturnResponseModel>(this.API, returnPayload).pipe(
      tap(response => console.log('Process Return Response:', response))
    );
  }

  // 2. GET RETURN BY ID
  getReturnById(id: string): Observable<ReturnResponseModel> {
    console.log('ReturnService getReturnById called with ID:', id);
    return this.http.get<ReturnResponseModel>(`${this.API}/${id}`).pipe(
      tap(response => console.log('Get Return By ID Response:', response))
    );
  }

  // 3. GET RETURNS (Role-based: Admin/Instructor sees all, Student sees theirs)
  getReturns(): Observable<ReturnResponseModel[]> {
    console.log('ReturnService getReturns called');
    return this.http.get<ReturnResponseModel[]>(this.API).pipe(
      tap(response => console.log('Get Returns Response:', response))
    );
  }

  // 4. GET RETURNS BY REQUEST ID
  getReturnsByRequest(requestId: string): Observable<ReturnResponseModel[]> {
    console.log('ReturnService getReturnsByRequest called with Request ID:', requestId);
    return this.http.get<ReturnResponseModel[]>(`${this.API}/request/${requestId}`).pipe(
      tap(response => console.log('Get Returns By Request Response:', response))
    );
  }
}