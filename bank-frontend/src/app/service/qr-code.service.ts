import { environment } from './../../environments/environment';
import { HttpClient } from '@angular/common/http';
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

}
