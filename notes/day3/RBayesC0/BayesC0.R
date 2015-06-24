#   This code is for illustrative purposes and not efficient for large problems
#   Real life data analysis (using the same file formats) is available at 
#   bigs.ansci.iastate.edu/login.html based on GenSel cpp software implementation
# 
#               Rohan Fernando      (rohan@iastate.edu)
#               Dorian Garrick      (dorian@iastate.edu) 
#               copyright August 2012

# Parameters
setwd("RBayesC0")
seed            =   10    # set the seed for the random number generator
chainLength     =  2000    # number of iterations
dfEffectVar     =    4    # hyper parameter (degrees of freedom) for locus effect variance          
nuRes           =    4    # hyper parameter (degrees of freedom) for residual variance
varGenotypic    =    1    # used to derive hyper parameter (scale) for locus effect variance 
varResidual     =    1    # used to derive hyper parameter (scale) for residual variance 
windowSize      =   10    # number of consecutive markers in a genomic window
outputFrequency =   100    # frequency for reporting performance and for computing genetic variances

markerFileName         = "genotypes.dat"
trainPhenotypeFileName = "trainPhenotypes.dat"
testPhenotypeFileName  = "testPhenotypes.dat"

set.seed(seed)

genotypeFile         = read.table(markerFileName, header=TRUE)                 # this is not efficient for large files!
trainPhenotypeFile   = read.table(trainPhenotypeFileName, skip=1)[,1:2]        # skip the header as R dislikes # character
testPhenotypeFile    = read.table(testPhenotypeFileName,  skip=1)[,1:2]        # skip the header as R dislikes # character
commonTrainingData   = merge(trainPhenotypeFile, genotypeFile, by.x=1, by.y=1) # Only use animals with genotype and phenotype
commonTestData       = merge(testPhenotypeFile,  genotypeFile, by.x=1, by.y=1) # Only use animals with genotype and phenotype


remove(genotypeFile)                                                # Free up space 
remove(trainPhenotypeFile)                                          # Free up space 
remove(testPhenotypeFile)                                           # Free up space 
animalID = unname(as.matrix(commonTrainingData[,1]))                # First field is animal identifier
y        = commonTrainingData[, 2]                                  # Second field is trait values
Z        = commonTrainingData[, 3: ncol(commonTrainingData)]        # Remaining fields are GenSel-coded genotypes 
Z        = unname(as.matrix((Z + 10)/10));                          # Recode genotypes to 0, 1, 2 (number of B alleles)
markerID = colnames(commonTrainingData)[3:ncol(commonTrainingData)] # Remember the marker locus identifiers
remove(commonTrainingData)

testID = unname(as.matrix(commonTestData[,1]))                  # First field is animal identifier
yTest        = commonTestData[, 2]                              # Second field is trait values
ZTest        = commonTestData[, 3: ncol(commonTestData)]        # Remaining fields are GenSel-coded genotypes 
ZTest        = unname(as.matrix((ZTest + 10)/10));              # Recode genotypes to 0, 1, 2 (number of B alleles)
remove(commonTestData)

nmarkers = ncol(Z)                                              # number of markers
nrecords = nrow(Z)                                              # number of animals

# center the genotype matrix to accelerate mixing 
markerMeans = colMeans(Z)                             # compute the mean for each marker
Z = t(t(Z) - markerMeans)                             # deviate covariate from its mean
p = markerMeans/2.0                                   # compute frequency B allele for each marker
mean2pq = mean(2*p*(1-p))                             # compute mean genotype variance

varEffects  = varGenotypic/(nmarkers*mean2pq)         # variance of locus effects is computed from genetic variance 
                                                      #(e.g. Fernando et al., Acta Agriculturae Scand Section A, 2007; 57: 192-195)
scaleVar    = varEffects*(dfEffectVar-2)/dfEffectVar; # scale factor for locus effects
scaleRes    = varResidual*(nuRes-2)/nuRes             # scale factor for residual variance

numberWindows = nmarkers/windowSize                   # number of genomic windows
numberSamples = chainLength/outputFrequency           # number of samples of genetic variances


alpha           = array(0.0, nmarkers) # reserve a vector to store sampled locus effects 
meanAlpha       = array(0.0, nmarkers) # reserve a vector to accumulate the posterior mean of locus effects
modelFreq       = array(0.0, nmarkers) # reserve a vector to store model frequency
mu              = mean(y)              # starting value for the location parameter 
meanMu          = 0                    # reserve a scalar to accumulate the posterior mean
geneticVar      = array(0,numberSamples) # reserve a vector to store sampled genetic variances
                                       # reserve a matrix to store sampled proportion proportion of variance due to window 
windowVarProp   = matrix(0,nrow=numberSamples,ncol=numberWindows)
sampleCount     = 0                    # initialize counter for number of samples of genetic variances
                  


# adjust y for the fixed effect (ie location parameter)
ycorr = y - mu


ZPZ=t(Z)%*%Z
zpz=diag(ZPZ)

ptime=proc.time()
# mcmc sampling
for (iter in 1:chainLength){
	
# sample residual variance
	vare = ( t(ycorr)%*%ycorr + nuRes*scaleRes )/rchisq(1,nrecords + nuRes)  
	
# sample intercept
	ycorr = ycorr + mu                    # Unadjust y for the previous sample of mu
	rhs    = sum(ycorr)                   # Form X'y
	invLhs = 1.0/nrecords                 # Form (X'X)-1
	mean = rhs*invLhs                     # Solve (X'X) mu = X'y                    
	mu = rnorm(1,mean,sqrt(invLhs*vare))  # Sample new location parameter                     
	ycorr = ycorr - mu                    # Adjust y for the new sample of mu
	meanMu = meanMu + mu                  # Accumulate the sum to compute posterior mean
	
# sample effect for each locus
	for (locus in 1:nmarkers){
		
		rhs=t(Z[,locus])%*%ycorr +zpz[locus]*alpha[locus]
		mmeLhs = zpz[locus] + vare/varEffects                        # Form the coefficient matrix of MME
		invLhs = 1.0/mmeLhs                                   # Invert the coefficient matrix 
		mean = invLhs*rhs                                     # Solve the MME for locus effect
		oldAlpha=alpha[locus]
		alpha[locus]= rnorm(1,mean,sqrt(invLhs*vare))         # Sample the locus effect from data
		ycorr = ycorr + Z[,locus]*(oldAlpha-alpha[locus]);               # Adjust the data for this locus effect
		meanAlpha[locus] = meanAlpha[locus] + alpha[locus];   # Accumulate the sum for posterior mean
	}
		
	# sample the common locus effect variance		
	varEffects = ( scaleVar*dfEffectVar + sum(alpha^2) )/rchisq(1,dfEffectVar+nmarkers)  
		
}

proc.time()-ptime
