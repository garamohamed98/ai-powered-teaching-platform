import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges} from '@angular/core';
import {Button} from "primeng/button";
import {Dialog} from "primeng/dialog";
import {InputText} from "primeng/inputtext";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {CourseCategory} from '../../models/course-category.model';
import {CourseCategoryService} from '../../services/course-category.service';
import {Select} from 'primeng/select';
import {CreateCourseDto} from '../../models/create-course.dto';
import {Course} from '../../models/course.model';
import {UpdateCourseDto} from '../../models/update-course.dto';

@Component({
  selector: 'app-courses-form-modal',
  imports: [
    Button,
    Dialog,
    InputText,
    ReactiveFormsModule,
    Select
  ],
  templateUrl: './courses-form-modal.component.html',
  styleUrl: './courses-form-modal.component.scss'
})
export class CoursesFormModalComponent implements OnInit, OnChanges {
  private coursesService = inject(CoursesService);
  private messageService = inject(MessageService);
  private courseCategoryService = inject(CourseCategoryService);
  private formBuilder = inject(FormBuilder);

  @Input({required:true}) isVisible!: boolean;
  @Output() isVisibleChange = new EventEmitter<boolean>();
  @Output() courseSaved = new EventEmitter<void>();
  @Input() selectedCourse: Course | null = null;

  categoryListLoading = signal<boolean>(false);
  categoryList = signal<CourseCategory[]>([]);

  visibility = [
    { value: "PUBLIC", label: "Public" },
    { value: "PRIVATE", label: "Private" },
  ];

  form: FormGroup = this.formBuilder.group({
    title: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
      ]
    ],
    categoryId: [
      null,
      [
        Validators.required
      ]
    ],
    visibility: [
      'PUBLIC',
      [
        Validators.required
      ]
    ]
  });

  ngOnInit(): void {
    this.loadCategoryList();
    this.loadForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['isVisible'] || changes['selectedCourse']) && this.isVisible) {
      this.loadForm();
    }
  }

  loadCategoryList(){
    this.categoryListLoading.set(true);
    this.courseCategoryService.getCourseCategoryList()
      .subscribe({
        next: (res) => {
          this.categoryList.set(res);
          this.categoryListLoading.set(false);
        },
        error: (error) => {
          this.categoryListLoading.set(false);
          console.log("Error: ", error.message);
        }
      });
  }

  loadForm(){
    if (this.selectedCourse != null) {
      this.form.reset({
        title: this.selectedCourse.title,
        categoryId: this.selectedCourse.category?.id ?? null,
        visibility: this.selectedCourse.visibility,
      });
    } else {
      this.form.reset({title: '', categoryId: null, visibility: 'PUBLIC'});
    }
  }

  close(){
    this.isVisible = false;
    this.isVisibleChange.emit(false);
  }

  submit(){
    if (!this.form.valid) return;

    const course = this.selectedCourse
    if(course != null){
      this.updateCourse(course);
    }else {
      this.createCourse();
    }
  }

  createCourse(){
    if (!this.form.valid) return;

    const { title, categoryId, visibility } = this.form.value;

    const createCourseDto: CreateCourseDto = {
      title,
      categoryId,
      visibility
    };

    this.coursesService.createCourse(createCourseDto).subscribe({
      next: (res) => {
        console.log("Course Created Successfully");
        this.messageService.add({
          severity: 'success',
          summary: 'New course is created successfully.',
          detail: `New Course is created with title ${res?.title}`,
          sticky: false,
        });
        this.courseSaved.emit();
        this.close();
      }
    });
  }

  updateCourse(course: Course){
    const { title, categoryId, visibility } = this.form.value;

    const updateCourseDto: UpdateCourseDto = {
      title: title != course.title ? title : undefined,
      categoryId: categoryId != course.category.id ? categoryId : undefined,
      visibility: visibility != course.visibility ? visibility : undefined
    };

    this.coursesService.updateCourse(course.id, updateCourseDto).subscribe({
      next: (res) => {
        console.log("Course Updated Successfully");
        this.messageService.add({
          severity: 'success',
          summary: 'course is updated successfully.',
          detail: `Course with title ${course.title} is updated`,
          sticky: false,
        });
        this.courseSaved.emit();
        this.close();
      }
    })
  }
}
