import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Course} from './models/course.model';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/course';

  constructor() {}

  getCourses() {
    return this.http.get<Course[]>(this.baseUrl);
  }
}
