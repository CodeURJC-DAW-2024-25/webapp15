import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-profile-data',
  templateUrl: './profile-data.component.html',
  styleUrls: ['./profile-data.component.css']
})
export class ProfileDataComponent implements OnInit {
  profileForm: FormGroup;
  userId: number | null = null;
  profileImage: SafeUrl | string = 'assets/images/default-profile.png';
  isAdmin: boolean = false;
  activeSection: string = 'profile-info';
  selectedFile: File | null = null;
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.userId = +params['id'];
      this.loadUserProfile();
    });

    // Check admin status - you'll need to implement your own logic
    this.checkAdminStatus();
  }

  loadUserProfile(): void {
    if (!this.userId) return;
    this.isLoading = true;

    this.http.get<any>(`/api/users/${this.userId}`)
      .subscribe({
        next: (user) => {
          this.profileForm.patchValue({
            firstName: user.firstName,
            lastName: user.lastName,
            username: user.username,
            email: user.email
          });
          this.loadProfileImage();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading profile:', err);
          this.isLoading = false;
        }
      });
  }

  loadProfileImage(): void {
    if (!this.userId) return;

    this.http.get(`/api/users/${this.userId}/image`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          const objectUrl = URL.createObjectURL(blob);
          this.profileImage = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
        },
        error: () => {
          this.profileImage = 'assets/images/default-profile.png';
        }
      });
  }

  checkAdminStatus(): void {
    // Implement your admin check logic here
    // Example: this.isAdmin = this.authService.isAdmin();
    this.isAdmin = false;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      
      // Create preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profileImage = this.sanitizer.bypassSecurityTrustUrl(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  updateProfile(): void {
    if (this.profileForm.invalid || !this.userId) return;
    this.isLoading = true;

    const formData = new FormData();
    formData.append('firstName', this.profileForm.value.firstName);
    formData.append('lastName', this.profileForm.value.lastName);
    formData.append('username', this.profileForm.value.username);
    formData.append('email', this.profileForm.value.email);

    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    this.http.put(`/api/users/${this.userId}`, formData)
      .subscribe({
        next: () => {
          // Success handling
          this.loadUserProfile(); // Refresh data
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error updating profile:', err);
          this.isLoading = false;
        }
      });
  }

  changePhoto(): void {
    document.getElementById('photoInput')?.click();
  }

  logout(): void {
    // Implement logout logic
    this.http.post('/api/logout', {}).subscribe(() => {
      // Redirect to login or home page
    });
  }

  navigateTo(section: string): void {
    this.activeSection = section;
  }
}