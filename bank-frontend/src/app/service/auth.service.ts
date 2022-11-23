import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import jwt_decode from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private _http: HttpClient) { }

  private auth_url = 'https://localhost:8080/api/auth';


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
      'name': user.name,
      'isCompany': user.isCompany
    };
    console.log(body)

    return this._http.post<any>(`${this.auth_url}/signup`, body)

  }


  
}
