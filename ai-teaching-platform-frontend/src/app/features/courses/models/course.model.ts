import {CourseCategory} from './course-category.model';

export interface Course {
  id: string;
  title: string;
  category: CourseCategory;
  visibility: string;
  createdAt: string;
}
