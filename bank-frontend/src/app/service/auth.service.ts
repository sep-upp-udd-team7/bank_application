import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import jwt_decode from "jwt-decode";
import { environment } from 'src/environments/environment';
import { Client } from '../model/client.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private _http: HttpClient) { }

  private auth_url = environment.backendUrl + 'clients';


  login(user) {
    const loginHeaders = new HttpHeaders({
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    });

    const body = {
      'email': user.email,
      'password': user.password
    };
    console.log(body)

    return this._http.post<any>(`${this.auth_url}/login`, body)
      .pipe(map((res: any) => {

        let decoded: any = jwt_decode(res.accessToken)
        localStorage.setItem("user", decoded.sub)
        localStorage.setItem("role", decoded.role)
        localStorage.setItem("jwt", res.accessToken);
        
      }));
  }

  signup(user){
    const body = {
      'email': user.email,
      'password': user.password,
      'reenteredPassword': user.reenteredPassword,
      'name': user.name,
      'isCompany': user.isCompany
    };
    console.log(body)

    return this._http.post<any>(`${this.auth_url}/registration`, body)
      .pipe(map((res: any) => {
        let decoded: any = jwt_decode(res.accessToken)
        localStorage.setItem("user", decoded.sub)
        localStorage.setItem("role", decoded.role)
        localStorage.setItem("jwt", res.accessToken);
        
      }));
  }

  getLoggedUser() {
    return this._http.get<Client>(`${this.auth_url}`);
  }
}
