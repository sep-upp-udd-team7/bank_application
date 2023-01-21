import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CreditCardService {

  constructor(private http: HttpClient) { }

  private bankAccountController = environment.backendUrl + 'accounts';

  validateIssuer(body: any) {
    const headers = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
   });
   let HTTPOptions: Object = {
    headers: headers,
    responseType: 'text'
 }
    return this.http.post<string>(`${this.bankAccountController}/validateIssuer`, body, HTTPOptions)
  }


}
