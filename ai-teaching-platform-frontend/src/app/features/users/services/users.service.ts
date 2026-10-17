import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private http = inject(HttpClient)
  private baseUrl = 'http://localhost:8080/api/user';

  constructor() { }

  getUser(){
    return this.http.get<{id:string,email:string,name:string}>(this.baseUrl)
  }
}
