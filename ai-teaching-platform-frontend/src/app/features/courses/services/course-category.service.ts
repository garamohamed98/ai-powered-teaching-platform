import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CourseCategory} from '../models/course-category.model';

@Injectable({
  providedIn: 'root'
})
export class CourseCategoryService {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/course-category';

  constructor() {}

  getCourseCategoryList(){
    console.log("fetching course category list");
    return this.http.get<CourseCategory[]>(`${this.baseUrl}`)
  }
}
