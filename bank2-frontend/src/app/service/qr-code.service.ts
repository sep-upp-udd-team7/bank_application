import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class QrCodeService {

  constructor(private http: HttpClient) { }

  private qrCodeController = environment.backendUrl + 'qr';

  getQrCodeData(paymentId) {
      return this.http.get<any>(`${this.qrCodeController}/getQrCodeData/` + paymentId);
  }

  getQrCodeImage(data: any){
   const headers = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
   });
   let HTTPOptions: Object = {
    headers: headers,
    responseType: 'text'

  }
    return this.http.post<any>(`${this.qrCodeController}/getQRCode`, data, HTTPOptions);
  }
}
