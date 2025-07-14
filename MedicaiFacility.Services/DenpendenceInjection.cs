
using MedicaiFacility.DataAccess;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Repository;
using MedicaiFacility.Service.IService;
using MedicaiFacility.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public static class DenpendenceInjection
    {
        public static IServiceCollection AddRepositories(this IServiceCollection services)
        {
            services.AddScoped<IHealthRecordRepository,HealthRecordRepository>();
            services.AddScoped<IDepartmentRepository, DepartmentRepository>();
            services.AddScoped<IDiseaseRepository, DiseaseRepository>();
            services.AddScoped<IMedicalFacilityRepository, MedicalFacilityRepository>();
            services.AddScoped<IFacilityDepartmentRepository, FacilityDepartmentRepository>();
            services.AddScoped<IHealthArticleRepository,HealthArticleRepository>();
			services.AddScoped<IUserRepository, UserRepository>();
			services.AddScoped<ITransactionRepository, TransactionRepository>();
            services.AddScoped<IAppointmentRepository, AppointmentRepository>();
            services.AddScoped<IMedicalHistoryRepository, MedicalHistoryRepository>();
            services.AddScoped<IMedicalExpertRepository, MedicalExpertRepository>();
            services.AddScoped<IPatientRepository, PatientRepository>();
            services.AddScoped<ISystemBalanceRepository, SystemBalanceRepository>();
            services.AddScoped<IHealthRecordDiseasesRepository, HealthRecordDiseasesRepository>();	
            services.AddScoped<IRatingsAndFeedbackRepository, RatingsAndFeedbackRepository>();       
            services.AddScoped<IMedicalExpertScheduleRepository, MedicalExpertScheduleRepository>();

            return services;
        }

        public static IServiceCollection AddServices(this IServiceCollection services)
        {
            services.AddScoped<IHealthRecordService, HealhRecordService>();
            services.AddScoped<IDepartmentService, DepartmentService>();
            services.AddScoped<IDiseaseService, DiseaseService>();
            services.AddScoped<IMedicalFacilityService, MedicalFacilityService>();
            services.AddScoped<IFacilityDepartmentService, FacilityDepartmentService>();
            services.AddScoped<IHealthArticleService, HealthArticleService>();
            services.AddScoped<IUserService, UserService>();
            services.AddScoped<ITransactionService, TransactionService>();
            services.AddScoped<IAppointmentService, AppointmentService>();
            services.AddScoped<IMedicalHistoryService, MedicalHistoryService>();
            services.AddScoped<IMedicalExpertService, MedicalExpertService>();
            services.AddScoped<IRatingsAndFeedbackService, RatingsAndFeedbackService>();  
            services.AddScoped<IPatientService, PatientService>();
            services.AddScoped<ISystemBalanceService, SystembalanceService>();
            services.AddScoped<IHealthRecordDiseasesService,HealthRecordDiseasesService>();
            services.AddScoped<IMedicalExpertScheduleService, MedicalExpertScheduleService>();
            return services;
        }


        public static IServiceCollection AddDatabaseAndConfiguration(this IServiceCollection services, IConfiguration configuration)
        {
            services.AddDbContext<AppDbContext>(options =>
                options.UseSqlServer(configuration.GetConnectionString("DefaultConnection")));

            services.AddSingleton<IConfiguration>(configuration);
            return services;
        }
    }
}
